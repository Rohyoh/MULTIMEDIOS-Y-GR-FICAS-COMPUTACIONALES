package up.edu.cg.images;

import java.awt.*;
import java.io.IOException;
import java.lang.Math;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

public class Main {
    public static void main(String[] args) {
        BufferedImage image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < 500; x++){ // we paint the background as a solid white block
            for (int y = 0; y < 500; y++){
                image.setRGB(x, y, Color.white.getRGB());
            }
        }
        //Wave part
        int width = 500; // wave's/image width
        int height = 500; // images heigh... needed to paint under the wave
        int centerY = 400; // where do I want the wave to begin in respect to the Y axis
        int amplitude = 20; // wave's height

        for(int k = 0; k < width; k++){ //We calculate sin in the image's width
            double angle = (double)k / width * 2 * Math.PI * 6; // k = x value, width = "in how many steps we will spread this values", 2Pi = sine function range, 5 = amount of waves
            int y = (int)(centerY - amplitude * Math.sin(angle)); //Let us remember, the image is an array of arrays to which, y axis grows downwards... that's why it's a substraction.
            image.setRGB(k, y, Color.green.getRGB()); // we paint the waves

            for(int n = y + 1; n < height; n++){ // we make a cicle from the wave's y coordinate of said cycle's step and color under it until it reaches the image's height
                image.setRGB(k, n, Color.green.getRGB());
            }
        }

        //Circle part (sun)
        int cX = 100, cY = 100, radius= 65, lightRay = 100; //Center of our circle int the x and y axis, and the circle's radius

        for (int h = 0; h < 360; h+=45) { // lightrays (for the sun), when make 6 total lightrays
            for (int j= 0; j<lightRay; j++ ){ // we cycle throught the lightrays vector magnitud to graph the whole line
                int pxlX = cX + ((int) (j * Math.sin(Math.toRadians((double)h))));
                int pxlY = cY + ((int) (j * Math.cos(Math.toRadians((double)h))));
                image.setRGB(pxlX, pxlY, Color.orange.getRGB()); //we paint it
            }
        }

        for(int x = 0; x<360; x++){ // Circles circumference
            int pxlX = cX + ((int) (radius * Math.sin(Math.toRadians((double)x))));//We calculate the x and y coordinates of the circunference
            int pxlY = cY + ((int) (radius * Math.cos(Math.toRadians((double)x))));
            image.setRGB(pxlX, pxlY, Color.yellow.getRGB());
        }

        for (int x = cX - radius; x <= cX + radius; x++) { // Instead of checking the whole image, we'll just check the box that bounds our circle
            for (int y = cY - radius; y <= cY + radius; y++) { // ^^^^^^^^^^^^^^^^
                int dx = x - cX; // we make the coordinates relative to the circle's center
                int dy = y - cY;

                if (dx * dx + dy * dy <= radius * radius) { // we use the circle formula x^2+y^2<=r^2... if x^2+y^2 is lower or equal to r^2 it means that said pixel is inside the circle
                    image.setRGB(x, y, Color.yellow.getRGB()); // we paint the valid points inside our box :))
                }
            }
        }

        File outputImage = new File("landscape.png"); // we create the file
        try{
            ImageIO.write(image, "png", outputImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}