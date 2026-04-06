public class consoleProcess {

    public consoleProcess(String PATH) {
        ProcessBuilder pb = new ProcessBuilder("Exiftool", "-s", PATH);
    }

}
