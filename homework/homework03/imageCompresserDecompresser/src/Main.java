import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        try {
            // We read the imagefile we aim to compress
            File inputImage = new File("src/input.png");
            BufferedImage img = ImageIO.read(inputImage);
            int width = img.getWidth();
            int height = img.getHeight();

            // We turn the image from a 2D array into a 1D array
            int[] pxls = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    pxls[y * width + x] = img.getRGB(x, y);
                }
            }

            // ENCODER <-- the one in charge of compressing
            Compress compressor = new Compress();
            String compressedFileName = "compressedImage.rle";
            compressor.compress(pxls, width, height, compressedFileName);
            System.out.println("Image was properly compressed");

            // DECODER <-- The one in charge of decompressing
            Decompress decompressor = new Decompress();
            decompressor.decompress(compressedFileName, "output.png");
            System.out.println("Decompressed image name: 'output.png'");

        } catch (IOException e) {
            System.out.println(
                    "Error ---- 'input.png' does not exist or its PATH is incorrect "
                            + e.getMessage()
            );
        }
    }
}
