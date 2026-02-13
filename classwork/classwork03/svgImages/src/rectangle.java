import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class rectangle {

    public void create() throws IOException {
        File file = new File("rectangle.svg");

        String content =
                "<svg width=\"400\" height=\"300\" viewBox=\"0 0 400 300\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                        "    <polygon points=\"1,1 400,1 400,300\" fill=\"#FF0000\" />\n" +
                        "    <polygon points=\"1,1 1,300 400,300\" fill=\"#0000FF\" />\n" +
                        "</svg>";

        FileWriter fw = new FileWriter(file);
        fw.write(content);
        fw.close();
    }
}
