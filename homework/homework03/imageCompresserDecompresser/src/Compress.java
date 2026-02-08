import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Compress {

    public void compress(int[] pxls, int width, int height, String path) throws IOException {
        try (DataOutputStream output = new DataOutputStream(new FileOutputStream(path))) {

            // Write image header
            output.writeInt(width);
            output.writeInt(height);

            // We take the first pixel and turn it gray (0-255) || 32 ARGB color values, 0-7 blue, 8-15 green 16-23 red and the remaining bits are for the alpha
            int currentColor = (pxls[0] >> 16) & 0xFF; //we mask the pixels as binary values, thats why we use the >> operator and hexaadecimal values
            int streak = 0;

            for (int i = 0; i < pxls.length; i++) {
                int pixel = pxls[i];
                int gray = (pixel >> 16) & 0xFF; // We turn the pixel gray... we extract the red channel's value and use it for everything, because if all the 3 channels have the same value = gray intensity

                if (gray == currentColor && streak < 255) {
                    streak++;
                } else {
                    output.writeByte(streak);       // 1 byte for frequency
                    output.writeByte(currentColor); // 1 byte for color

                    currentColor = gray;
                    streak = 1;
                }
            }

            // Write last streak
            output.writeByte(streak);
            output.writeByte(currentColor);
        }
    }
}