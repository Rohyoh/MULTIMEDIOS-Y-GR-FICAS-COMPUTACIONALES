import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;

public class apiHandler {

    // We construct the prompt for the first image (creative image that depicts the provided images/videos)
    public String buildImagePrompt(mediaFile[] media) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Create a single artistic image that represents the essence of the following media files:\n\n");

        for (int i = 0; i < media.length; i++) {
            prompt.append("File ").append(i + 1).append(":\n");
            prompt.append("GPS: ").append(media[i].GPS).append("\n");
            prompt.append("Date: ").append(media[i].date).append("\n\n");
        }

        prompt.append("""
        The image should feel cohesive and symbolic, blending all moments into one scene.
        Style: semi-realistic, cinematic lighting.
        """);

        return prompt.toString();
    }

    // We construct the prompt for the second image (map image of the oldest and newest picture/video)
    public String buildMapPrompt(mediaFile oldest, mediaFile newest) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Create a simple geographic map.\n\n");

        prompt.append("Oldest file location:\n");
        prompt.append("GPS: ").append(oldest.GPS).append("\n");
        prompt.append("Date: ").append(oldest.date).append("\n\n");

        prompt.append("Newest file location:\n");
        prompt.append("GPS: ").append(newest.GPS).append("\n");
        prompt.append("Date: ").append(newest.date).append("\n\n");

        prompt.append("""
        The map should:
        - Be geographically accurate
        - Show land and terrain
        - Contain two red dots:
          • one for the oldest file
          • one for the newest file
        - No need for street names
        - Clean and minimal style
        - Add a motivational quote based of the locations
        """);

        return prompt.toString();
    }

    // We create the prompt for the narration (will be used as a script)
    public String buildScriptPrompt(mediaFile[] media) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Create a narration script describing the following visual media files.\n\n");
        prompt.append("Important: analyze the visual content itself, not only the metadata.\n\n");

        for (int i = 0; i < media.length; i++) {
            prompt.append("File ").append(i + 1).append(":\n");
            prompt.append("GPS: ").append(media[i].GPS).append("\n");
            prompt.append("Date: ").append(media[i].date).append("\n\n");
        }

        prompt.append("""
        Instructions:
        - Describe only the most remarkable element of only what is visually observable
        - Less than 10 words per image
        - Do NOT mention dates, GPS, or metadata
        - Do NOT use labels like "script", "image", or "file"
        - Use very concise language (5–10 words per image)
        - One short sentence per media element
        - No lists, no numbering
        - Keep it natural and fluid as narration
        - Describe the media in chronological order
        - Keep it concise (less than 10 words per image)
        - Use the media content as the main source of truth
        """);

        return prompt.toString();
    }

    // We generate narration using the actual images / extracted video frames
    public String generateTextFromMedia(mediaFile[] media, String apiKey) {
        try {
            String prompt = buildScriptPrompt(media);
            String json = buildMultimodalChatJson(prompt, media);
            return sendJsonToChatCompletions(json, apiKey);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // We generate an image utilizing curl
    public void generateImage(String prompt, String apiKey, String outputName){

        // escape the Json
        String escapedPrompt = prompt
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");

        String json = """
        {
          "model": "gpt-image-1",
          "prompt": "%s",
          "size": "1536x1024"
        }
        """.formatted(escapedPrompt);

        File tempFile;

        try {
            tempFile = File.createTempFile("openai_image", ".json");
            tempFile.deleteOnExit();

            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            writer.write(json);
            writer.close();

        } catch (IOException e) {
            System.out.println("Error creating temp file.");
            return;
        }

        String[] command = {
                "curl",
                "-s",
                "-X", "POST",
                "https://api.openai.com/v1/images/generations",
                "-H", "Content-Type: application/json",
                "-H", "Authorization: Bearer " + apiKey,
                "-d", "@" + tempFile.getAbsolutePath()
        };

        try {
            Process process = new ProcessBuilder().command(command).start();

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            StringBuilder responseBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }

            process.waitFor();

            String response = responseBuilder.toString();

            // We extract the image's base 64
            if (!response.contains("b64_json")) {
                System.out.println("API Error Response:\n" + response);
                return;
            }

            String base64Image = response.replaceAll(
                    ".*\"b64_json\"\\s*:\\s*\"(.*?)\".*",
                    "$1"
            );

            // we convert it into bytes
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            // We save the file
            FileOutputStream fos = new FileOutputStream(outputName);
            fos.write(imageBytes);
            fos.close();

            System.out.println("Image saved as " + outputName);

        } catch (Exception e) {
            throw new RuntimeException(e); // In case of something funny, we catch
        }
    }

    // We generate text using curl
    public String generateText(String prompt, String apiKey) {

        String escapedPrompt = prompt
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");

        String json = """
    {
      "model": "gpt-4o-mini",
      "messages": [
        {"role": "user", "content": "%s"}
      ]
    }
    """.formatted(escapedPrompt);

        try {
            File tempFile = File.createTempFile("openai_text", ".json");
            tempFile.deleteOnExit();

            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            writer.write(json);
            writer.close();

            String[] command = {
                    "curl",
                    "-s",
                    "-X", "POST",
                    "https://api.openai.com/v1/chat/completions",
                    "-H", "Content-Type: application/json",
                    "-H", "Authorization: Bearer " + apiKey,
                    "-d", "@" + tempFile.getAbsolutePath()
            };

            Process process = new ProcessBuilder().command(command).start();

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            StringBuilder responseBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }

            process.waitFor();

            String response = responseBuilder.toString();

            // We extract the text
            return response.replaceAll(
                    ".*\"content\"\\s*:\\s*\"(.*?)\".*",
                    "$1"
            );

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // We generate Audio using curl
    public void generateAudio(String text, String apiKey, String outputName) {

        String escapedText = text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", " ");

        String json = """
    {
      "model": "gpt-4o-mini-tts",
      "voice": "alloy",
      "input": "%s"
    }
    """.formatted(escapedText);

        try {
            File tempFile = File.createTempFile("openai_audio", ".json");
            tempFile.deleteOnExit();

            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            writer.write(json);
            writer.close();

            String[] command = {
                    "curl",
                    "-s",
                    "-X", "POST",
                    "https://api.openai.com/v1/audio/speech",
                    "-H", "Content-Type: application/json",
                    "-H", "Authorization: Bearer " + apiKey,
                    "-d", "@" + tempFile.getAbsolutePath()
            };

            Process process = new ProcessBuilder().command(command).start();

            InputStream inputStream = process.getInputStream();
            FileOutputStream fos = new FileOutputStream(outputName);

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }

            process.waitFor();
            fos.close();

            System.out.println("Audio saved as " + outputName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String buildMultimodalChatJson(String prompt, mediaFile[] media) throws Exception {
        StringBuilder content = new StringBuilder();
        content.append("[");

        content.append(textPart(prompt));

        for (int i = 0; i < media.length; i++) {
            String visualPath = getVisualPathForAnalysis(media[i].mediaPath);
            String dataUrl = fileToDataUrl(new File(visualPath));

            content.append(",");
            content.append(imagePart(dataUrl));

            content.append(",");
            content.append(textPart("File " + (i + 1) + " metadata: GPS " + safe(media[i].GPS) + ", Date " + safe(media[i].date) + "."));
        }

        content.append("]");

        return """
        {
          "model": "gpt-4o-mini",
          "messages": [
            {
              "role": "user",
              "content": %s
            }
          ],
          "temperature": 0.7
        }
        """.formatted(content.toString());
    }

    private String sendJsonToChatCompletions(String json, String apiKey) throws Exception {
        File tempFile = File.createTempFile("openai_text_mm", ".json");
        tempFile.deleteOnExit();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write(json);
        }

        String[] command = {
                "curl",
                "-s",
                "-X", "POST",
                "https://api.openai.com/v1/chat/completions",
                "-H", "Content-Type: application/json",
                "-H", "Authorization: Bearer " + apiKey,
                "-d", "@" + tempFile.getAbsolutePath()
        };

        Process process = new ProcessBuilder().command(command).start();

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));

        StringBuilder responseBuilder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            responseBuilder.append(line);
        }

        process.waitFor();

        String response = responseBuilder.toString();

        if (!response.contains("\"content\"")) {
            System.out.println("API Error Response:\n" + response);
            return "";
        }

        return response.replaceAll(
                ".*\"content\"\\s*:\\s*\"(.*?)\".*",
                "$1"
        );
    }

    private String getVisualPathForAnalysis(String path) throws Exception {
        if (isVideo(path)) {
            return extractRepresentativeFrame(path);
        }
        return path;
    }

    private String extractRepresentativeFrame(String videoPath) throws Exception {
        File frame = File.createTempFile("openai_frame_", ".jpg");
        frame.deleteOnExit();

        String[] command = {
                "ffmpeg",
                "-y",
                "-ss", "00:00:01",
                "-i", videoPath,
                "-frames:v", "1",
                "-q:v", "2",
                frame.getAbsolutePath()
        };

        Process process = new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();

        String ffmpegOutput = readProcessOutput(process);
        int exitCode = process.waitFor();

        if (exitCode != 0 || frame.length() == 0) {
            throw new IOException("Could not extract a frame from video: " + videoPath + "\n" + ffmpegOutput);
        }

        return frame.getAbsolutePath();
    }

    private String fileToDataUrl(File file) throws IOException {
        String mimeType = mimeTypeFor(file.getName());
        byte[] bytes = Files.readAllBytes(file.toPath());
        String base64 = Base64.getEncoder().encodeToString(bytes);
        return "data:" + mimeType + ";base64," + base64;
    }

    private String mimeTypeFor(String fileName) {
        String lower = fileName.toLowerCase();

        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".webp")) return "image/webp";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".bmp")) return "image/bmp";
        if (lower.endsWith(".heic")) return "image/heic";
        if (lower.endsWith(".tif") || lower.endsWith(".tiff")) return "image/tiff";

        return "application/octet-stream";
    }

    private String textPart(String text) {
        return """
        {"type":"text","text":"%s"}
        """.formatted(jsonEscape(text));
    }

    private String imagePart(String dataUrl) {
        return """
        {"type":"image_url","image_url":{"url":"%s","detail":"high"}}
        """.formatted(jsonEscape(dataUrl));
    }

    private boolean isVideo(String path) {
        String lower = path.toLowerCase();
        return lower.endsWith(".mp4") ||
                lower.endsWith(".mov") ||
                lower.endsWith(".avi") ||
                lower.endsWith(".mkv") ||
                lower.endsWith(".webm") ||
                lower.endsWith(".m4v");
    }

    private String jsonEscape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "")
                .replace("\n", "\\n");
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String readProcessOutput(Process process) throws IOException {
        try (InputStream inputStream = process.getInputStream();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }

            return output.toString(StandardCharsets.UTF_8);
        }
    }
}