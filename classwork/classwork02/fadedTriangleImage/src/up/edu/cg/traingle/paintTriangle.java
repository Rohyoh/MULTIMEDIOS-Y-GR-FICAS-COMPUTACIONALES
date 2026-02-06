package up.edu.cg.traingle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class paintTriangle {
    int boxHeight = 500;// We define the width and height of the image
    int boxWidth = 500;
    BufferedImage image = new BufferedImage(boxWidth, boxHeight, BufferedImage.TYPE_INT_RGB);

    lambda calc;
    public paintTriangle( ) { //the constructor does all the heavy lifting
        calc = new lambda();
        paintBaseImage(boxHeight, boxWidth);
        paintTriangleFull();
    }

    private void paintBaseImage(int height, int width){ // we paint the image base color
        for (int j = 0; j<height; j++) {
            for (int k = 0; k<width; k++) {
                image.setRGB(k, j, Color.white.getRGB());
            }
        }
    }

    private void paintTriangleFull() { //we iterate through all the image to paint our triangle
        for (int y = 0; y < boxHeight; y++) {
            for (int x = 0; x < boxWidth; x++) {

                float lambda1 = calc.calculateLambda(1, x, y); //We use the method we made to calculate each lambda's value
                float lambda2 = calc.calculateLambda(2, x, y);
                float lambda3 = 1 - lambda1 - lambda2;

                float eps = 0.0001f; // This helps us to much better corroborate that the sum of all lambdas is = 1

                if (lambda1 >= 0 && lambda2 >= 0 && lambda3 >= 0 && Math.abs(lambda1 + lambda2 + lambda3 - 1) < eps) { // we filter out the invalid pixels and paint the valid ones
                    int r = (int)(lambda1 * 255); // this helps us to determine how much of a color's value is in each part of the triangle
                    int g = (int)(lambda2 * 255);
                    int b = (int)(lambda3 * 255);

                    image.setRGB(x, y, new Color(r, g, b).getRGB()); //we paint it
                }
            }
        }
        saveImage(image, "triangle", "png"); // we save it
    }


    public static void saveImage(BufferedImage image, String fileName, String fileType) {
        File file = new File(fileName + "." + fileType);
        try {
            ImageIO.write(image, fileType, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
