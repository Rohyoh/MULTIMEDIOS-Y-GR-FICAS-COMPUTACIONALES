import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        String apiKey = System.getenv("OpenAIToken"); //We search for the API key in the enviroment variables

        if (apiKey == null) {
            System.out.println("Environment variable 'OpenAIToken' not found"); //We throw an error in case of invalid API key
            return;
        }

        System.out.println("Please specify the amount of files you want to process: "); //we ask for the amount of files to process
        int amount = sc.nextInt();
        sc.nextLine();

        mediaFile[] media = new mediaFile[amount];

        for (int i = 0; i < amount; i++) {
            System.out.println("Provide path for file " + (i + 1) + ":");
            String path = sc.nextLine();
            media[i] = new mediaFile(path);
        }

        // We filter by date
        mediaUtils.sortByDate(media);

        // We determine oldest & newest
        mediaFile oldest = mediaUtils.getOldest(media);
        mediaFile newest = mediaUtils.getNewest(media);

        apiHandler api = new apiHandler();

        // We create the first image (essence)
        System.out.println("\n[Processing] Creating the essence image...");
        String artisticPrompt = api.buildImagePrompt(media);
        api.generateImage(artisticPrompt, apiKey, "essenceImage.png");

        // We create the second image (map)
        System.out.println("\n[Processing] Creating the map image...");
        String mapPrompt = api.buildMapPrompt(oldest, newest);
        api.generateImage(mapPrompt, apiKey, "mapImage.png");

        // We generate the script from the actual images / frames
        System.out.println("\n[Processing] Generating narration script...");
        String narrationText = api.generateTextFromMedia(media, apiKey);

        System.out.println("\nGenerated Script:\n" + narrationText);

        // We generate audio
        System.out.println("\n[Processing] Generating audio narration...");
        api.generateAudio(narrationText, apiKey, "narration.mp3");

        // We create the base video
        System.out.println("\n[Processing] Creating video segments...");
        videoCreator vc = new videoCreator();
        vc.createVideo(media, "narration.mp3");

        // We ask for final video name
        System.out.println("\nEnter final video name (without .mp4):");
        String finalName = sc.nextLine();

        // we convert to portrait format
        System.out.println("\n[Processing] Converting to portrait format with blur effect...");
        vc.convertToPortrait("final_video.mp4", finalName + ".mp4");

        // We provide the option to verify audio levels regarding the specified metrics
        System.out.println("\nDo you want to verify audio loudness levels? (yes/no):");
        String verify = sc.nextLine().trim().toLowerCase();

        sc.close(); // Important to close the scanner at the very end

        if (verify.equals("yes") || verify.equals("y")) {
            vc.verifyAudioLevels(finalName + ".mp4");
        }

        System.out.println("\nProcess completed successfully :D!");
        System.out.println("Final video: " + finalName + ".mp4");
    }
}