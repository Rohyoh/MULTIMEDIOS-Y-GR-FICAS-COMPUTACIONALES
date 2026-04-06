import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class videoCreator {

    // Video creating method, an absolute unit! :D
    public void createVideo(mediaFile[] media, String audioPath) {

        try {
            List<File> segments = new ArrayList<>();

            // We normalize the opening image
            System.out.println("  - Processing opening image...");
            File essenceImage = new File("essenceImage.png");
            if (!essenceImage.exists()) {
                System.out.println("    WARNING: essenceImage.png not found!");
            }
            segments.add(createImageSegment(essenceImage, 3, "segment_000.mp4"));

            // Users files (already organized)
            for (int i = 0; i < media.length; i++) {
                File source = new File(media[i].mediaPath);
                File segment;

                if (isVideo(source.getAbsolutePath())) {
                    System.out.println("  - Processing video file " + (i + 1) + "/" + media.length + "...");
                    segment = createVideoSegment(source, "segment_" + (i + 1) + ".mp4");
                } else {
                    System.out.println("  - Processing image file " + (i + 1) + "/" + media.length + "...");
                    segment = createImageSegment(source, 3, "segment_" + (i + 1) + ".mp4");
                }

                segments.add(segment);
            }

            // closing map image
            System.out.println("  - Processing closing map...");
            File mapImage = new File("mapImage.png");
            if (!mapImage.exists()) {
                System.out.println("    WARNING: mapImage.png not found!");
            }
            segments.add(createImageSegment(mapImage, 3, "segment_last.mp4"));

            // We build the concat file using only normalized MP4 segments
            File listFile = new File("input.txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(listFile));

            for (File segment : segments) {
                writer.write("file '" + segment.getAbsolutePath().replace("\\", "/") + "'\n");
            }

            writer.close();

            // Debug: show total segments
            System.out.println("  - Total segments created: " + segments.size());

            // We run FFmpeg
            System.out.println("  - Compiling final video with audio...");
            runFFmpeg(audioPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // We generate a normalized video segment from a still image (horizontal format)
    private File createImageSegment(File imageFile, int durationSeconds, String outputName) throws Exception {
        File output = new File(outputName);

        String[] command = {
                "ffmpeg",
                "-y",
                "-loop", "1",
                "-i", imageFile.getAbsolutePath(),
                "-t", String.valueOf(durationSeconds),
                "-vf", "scale=1920:1080:force_original_aspect_ratio=increase,crop=1920:1080,format=yuv420p",
                "-r", "30",
                "-c:v", "libx264",
                output.getAbsolutePath()
        };

        runCommand(command, "image segment");
        return output;
    }

    // We generate a normalized video segment from a user video (horizontal format)
    private File createVideoSegment(File videoFile, String outputName) throws Exception {
        File output = new File(outputName);

        String[] command = {
                "ffmpeg",
                "-y",
                "-i", videoFile.getAbsolutePath(),
                "-vf", "scale=1920:1080:force_original_aspect_ratio=increase,crop=1920:1080,format=yuv420p",
                "-r", "30",
                "-c:v", "libx264",
                "-an",
                output.getAbsolutePath()
        };

        runCommand(command, "video segment");
        return output;
    }

    // We determine if it's a video or not based of its suffix
    private boolean isVideo(String path) {
        String lower = path.toLowerCase();
        return lower.endsWith(".mp4") || lower.endsWith(".mov") || lower.endsWith(".avi") || lower.endsWith(".mkv") || lower.endsWith(".webm") || lower.endsWith(".m4v");
    }

    // We run FFmpeg to create the final video
    private void runFFmpeg(String audioPath) throws Exception {

        String[] command = {
                "ffmpeg",
                "-y",
                "-f", "concat",
                "-safe", "0",
                "-i", "input.txt",
                "-itsoffset", "3",  // Delay audio by 3 seconds to start after opening image
                "-i", new File(audioPath).getAbsolutePath(),
                "-c:v", "libx264",
                "-c:a", "aac",
                "-b:a", "192k",  // Audio bitrate for better quality
                "-af", "loudnorm=I=-14:TP=-1:LRA=7",  // YouTube loudness standards
                "-pix_fmt", "yuv420p",
                "final_video.mp4"
        };

        int exitCode = runCommand(command, "final video");

        if (exitCode == 0) {
            System.out.println("Video created successfully: final_video.mp4");
            System.out.println("  - Audio normalized to the following standards (-14 LUFS, -1 dBTP, 7 LRA)");
        } else {
            System.out.println("FFmpeg failed while creating the final video.");
        }
    }

    // We verify the audio loudness levels, kinda optional
    public void verifyAudioLevels(String videoPath) {
        System.out.println("\n[Verifying] Checking audio loudness levels...");

        try {
            String[] command = {
                    "ffmpeg",
                    "-i", new File(videoPath).getAbsolutePath(),
                    "-af", "loudnorm=print_format=summary",
                    "-f", "null",
                    "-"
            };

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            boolean inSummary = false;

            //Suffix
            while ((line = reader.readLine()) != null) {
                if (line.contains("Input Integrated:") || line.contains("Input True Peak:") || line.contains("Input LRA:") || line.contains("Output Integrated:") || line.contains("Output True Peak:") || line.contains("Output LRA:")) {
                    System.out.println("  " + line.trim());
                    inSummary = true;
                }
            }

            process.waitFor();

            if (inSummary) {
                System.out.println("\nStandards:");
                System.out.println("  Target Loudness: -16 to -14 LUFS");
                System.out.println("  Target True Peak: -2 to -1 dBTP");
                System.out.println("  Target LRA: 5 to 10 LU");
            }

        } catch (Exception e) {
            System.out.println("Could not verify audio levels: " + e.getMessage());
        }
    }

    // We run external commands and capture the exit code
    private int runCommand(String[] command, String label) throws Exception {
        Process process = new ProcessBuilder(command) //to keep the user informed of what's going on
                .inheritIO()
                .start();

        int exitCode = process.waitFor();

        if (exitCode != 0) { // In case of anything funny
            throw new RuntimeException("FFmpeg failed while processing " + label + " with exit code " + exitCode);
        }

        return exitCode;
    }

    // We convert the video to portrait format
    public void convertToPortrait(String inputVideo, String outputVideo) {

        try {

            String[] command = {
                    "ffmpeg",
                    "-y",
                    "-i", new File(inputVideo).getAbsolutePath(),
                    "-lavfi",
                    "[0:v]scale=1080:1920:force_original_aspect_ratio=increase," +
                            "crop=1080:1920," +
                            "boxblur=20:10[bg];" +
                            "[0:v]scale=1080:1920:force_original_aspect_ratio=decrease[fg];" +
                            "[bg][fg]overlay=(W-w)/2:(H-h)/2",
                    "-c:a", "copy",
                    new File(outputVideo).getAbsolutePath()
            };

            Process process = new ProcessBuilder(command).inheritIO().start();

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Final video created: " + outputVideo);
            } else {
                System.out.println("Portrait conversion failed.");
            }

        } catch (Exception e) {
            e.printStackTrace(); // In case of something funny
        }
    }
}