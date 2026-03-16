import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        String apiKey = System.getenv("OpenAIToken"); //We search for the API key in the enviroment variables

        if (apiKey == null) {
            System.out.println("Environment variable 'OpenAIToken' not found"); //We throw an error in case of invalid API key
            return;
        }

        System.out.println("Please provide the file's PATH (without commas): "); //we ask for the text's file PATH
        String filePath = sc.nextLine();

        System.out.println("Please provide the desired language to translate to: "); //we ask for the language the user desires to transalate the text
        String language = sc.nextLine();

        StringBuilder textBuilder = new StringBuilder(); //We create a StringBuilder instead of a common String to be able to modify it later on

        // We read the indicated text file
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(filePath)); //The buffer is instanciated with the file's PATH
            String line;

            while ((line = fileReader.readLine()) != null) {
                textBuilder.append(line).append("\n"); //It concatenates all the file's contents
            }

            fileReader.close(); //We close the reader

        } catch (IOException e) {
            System.out.println("Error while reading file"); //we close the program in case there's no valid text file or it suffers an error
            return;
        }

        String text = textBuilder.toString(); //We cast the textBuilder variable into a normal string

        String prompt = "Translate the following text to " + language +
                ". Only output the translation.\n\n" + text; //We create an inmutable string to send the prompt to chat gpt

        // Escape JSON
        String escapedPrompt = prompt
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t"); //JSON has characters like the commas " that could end the string earlier, so, we should scape them

        String json = """
        {
          "model": "gpt-4o",
          "messages": [{"role": "user", "content": "%s"}]
        }
        """.formatted(escapedPrompt); //We build the JSON, clean and simple

        File tempFile; // We create a temporal file which will contain the JSON and which the CURL will read

        try {
            tempFile = File.createTempFile("openai_request", ".json");
            tempFile.deleteOnExit();//we nuke the temp file

            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            writer.write(json);
            writer.close();

        } catch (IOException e) {
            System.out.println("Error creating temp file."); //we return an error in case of an unexpected event
            return;
        }

        String[] command = {
                "curl",
                "-s",
                "-X", "POST",
                "https://api.openai.com/v1/chat/completions",
                "-H", "Content-Type: application/json",
                "-H", "Authorization: Bearer " + apiKey,
                "-d", "@" + tempFile.getAbsolutePath()
        }; //We create our program's CURL call

        ProcessBuilder builder = new ProcessBuilder(); //we create the process builder which will make all the heavy lifting for the call

        try {
            Process process = builder.command(command).start(); //We provide our builder with the command for the call

            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            StringBuilder responseBuilder = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                responseBuilder.append(line); //we read line by line the response from ChatGPT
            }
            process.waitFor();
            String response = responseBuilder.toString(); //we create a string with the response

            String translation = response.replaceAll(
                    ".*\"content\"\\s*:\\s*\"(.*?)\".*", //we extract ONLY what comes after 'content:' until the closing comma
                    "$1" //we replace the FULL response with the previous regex
            );

            BufferedWriter writer = new BufferedWriter(new FileWriter("translated.txt")); //we create a new file wich will contain the translated text
            writer.write(translation); //we write the translation into the file
            writer.close(); //we close the writer

            System.out.println("Translation saved to translated.txt"); //we send the closing message

        } catch (IOException | InterruptedException e) { //in case of something funny, emergency break
            throw new RuntimeException(e);
        }
    }
}