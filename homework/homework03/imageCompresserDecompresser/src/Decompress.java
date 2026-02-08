import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Decompress {

    public void decompress(String pathInput, String pathOutput) throws IOException {
        try (DataInputStream input = new DataInputStream(new FileInputStream(pathInput))) {
            // Explanation for above ^^^^ We create a DataInputStream object which is being initialized with a FileInputStream object
            //which is also being initialized with the variable "pathInput"... FileInputStream(pathInput) ---> creates a stream that reads bits directly from the program
            //pathInput ---> is the path of the .rle file in which we compressed the original image
            //DataInputStream ---> reads the data from readInt() and readByte(), instead of reading bytes it can read various data types (ints, floats, etc)
            //try ---> Guarantees that both DataInputStream and FileInputStream close at the end.

            // Read header
            int width = input.readInt();
            int height = input.readInt();

            BufferedImage imgOutput = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            int pxlTotal = width * height;
            int writtenPxl = 0;

            // Read 'till the complete image is processed
            while (writtenPxl < pxlTotal) {
                int streak = input.readUnsignedByte();
                int gray = input.readUnsignedByte();
                                    // 150 in binnary and how it moves through the bytes vvvv
                int blue  = gray; // 00000000000000000000000010010110
                int green = gray << 8; // 00000000000000001001011000000000
                int red   = gray << 16; // 00000000100101100000000000000000
                int alpha = 0xFF << 24; // 11111111000000000000000000000000
                int rgb = red + green + blue + alpha; // we sum up the values to obtain our a(rgb)

                for (int i = 0; i < streak; i++) {
                    int x = writtenPxl % width;
                    int y = writtenPxl / width;
                    imgOutput.setRGB(x, y, rgb);
                    writtenPxl++;
                }
            }

            ImageIO.write(imgOutput, "png", new File(pathOutput));
        }
    }
}
