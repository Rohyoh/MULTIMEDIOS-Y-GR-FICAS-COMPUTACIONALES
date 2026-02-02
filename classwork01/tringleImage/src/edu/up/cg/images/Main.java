package edu.up.cg.images;
//me la rife B))
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        BufferedImage image = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
//        image.setRGB(200, 200, Color.yellow.getRGB());

        for(int x = 0; x < 400; x++){
            for(int y = 0; y < 400; y++){
                image.setRGB(x, y, Color.blue.getRGB());
            }
        }
        int p = 1;
        for(int x = 0; x < 400; x++){
            for(int y = 0; y < p; y++){
                image.setRGB(x, y, Color.red.getRGB());
            }
            p++;
        }

        File outputImage = new File("output.png");
        try {
            ImageIO.write(image, "png", outputImage);
        } catch(IOException e){
            throw new RuntimeException(e);
        }
    }
}
