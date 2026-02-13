import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class landscape {

    public void create() throws IOException {
        File file = new File("landscape.svg");

        String content =
                "<svg width=\"400\" height=\"300\" viewBox=\"0 0 400 300\" xmlns=\"http://www.w3.org/2000/svg\">\n" +

                        "    <rect x=\"1\" y=\"1\" width=\"400\" height=\"300\" fill=\"#FFFF\" />\n" +

                        "    <line x1=\"30\" y1=\"75\" x2=\"130\" y2=\"75\" stroke=\"#FFBF00\" stroke-width=\"1\"/>\n" +
                        "    <line x1=\"80\" y1=\"25\" x2=\"80\" y2=\"125\" stroke=\"#FFBF00\" stroke-width=\"1\"/>\n" +
                        "    <line x1=\"40\" y1=\"35\" x2=\"120\" y2=\"115\" stroke=\"#FFBF00\" stroke-width=\"1\"/>\n" +
                        "    <line x1=\"40\" y1=\"115\" x2=\"120\" y2=\"35\" stroke=\"#FFBF00\" stroke-width=\"1\"/>\n" +

                        "    <circle cx=\"80\" cy=\"75\" r=\"32\" fill=\"#FFFF00\" />\n" +

                        "    <rect x=\"1\" y=\"270\" width=\"400\" height=\"30\" fill=\"#00FF00\" />\n" +

                        "    <path d=\"M 1 270 L 50 270 Q 25 200 1 270 Z\" fill=\"#00FF00\" />\n" +
                        "    <path d=\"M 50 270 L 100 270 Q 75 200 50 270 Z\" fill=\"#00FF00\" />\n" +
                        "    <path d=\"M 100 270 L 150 270 Q 125 200 100 270 Z\" fill=\"#00FF00\" />\n" +
                        "    <path d=\"M 150 270 L 200 270 Q 175 200 150 270 Z\" fill=\"#00FF00\" />\n" +
                        "    <path d=\"M 200 270 L 250 270 Q 225 200 200 270 Z\" fill=\"#00FF00\" />\n" +
                        "    <path d=\"M 250 270 L 300 270 Q 275 200 250 270 Z\" fill=\"#00FF00\" />\n" +
                        "    <path d=\"M 300 270 L 350 270 Q 325 200 300 270 Z\" fill=\"#00FF00\" />\n" +
                        "    <path d=\"M 350 270 L 400 270 Q 375 200 350 270 Z\" fill=\"#00FF00\" />\n" +

                        "</svg>";

        FileWriter fw = new FileWriter(file);
        fw.write(content);
        fw.close();
    }
}
