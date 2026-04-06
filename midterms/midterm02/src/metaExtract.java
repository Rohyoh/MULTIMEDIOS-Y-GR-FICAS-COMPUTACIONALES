import java.io.BufferedReader;
import java.io.InputStreamReader;

public class metaExtract {

    public String metaExtractGPS(String path) {
        // We search specifically for GPSPosition
        return runExiftool(path, "-GPSPosition");
    }

    public String metaExtractDate(String path) {
        // Search for CreateDate or DateTimeOriginal
        return runExiftool(path, "-CreateDate");
    }

    private String runExiftool(String path, String tag) {
        // we use -s3 to JUST get the value back
        ProcessBuilder pb = new ProcessBuilder("exiftool", "-s3", tag, path);
        try {
            Process dataProcess = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(dataProcess.getInputStream()));
            // we read the exiftool's output

            String result = reader.readLine(); // We read or response line

            dataProcess.waitFor();
            return (result != null) ? result.trim() : "No data"; // we indicate the no data error in such case
        } catch (Exception e) {
            return "Error: " + e.getMessage(); // In case something funny happens
        }
    }
}