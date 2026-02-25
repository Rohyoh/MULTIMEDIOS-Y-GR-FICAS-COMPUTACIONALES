import java.awt.image.BufferedImage;
import java.util.Scanner;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class rotate {
    private points A, B, C, D; //we store the 4 pairs of points that we will rotate
    int tempX = 0, tempX2=0, tempY = 0, tempY2=0, zero=0; //temporal variable
    Scanner sc = new Scanner(System.in);

    public rotate() {
        assignPoints();
    }
    private void assignPoints() { //asks the user the desired points to modify
        System.out.print("xA, yA |-------------| xB, yB\n       |             |\n       |             |\n       |             |\n       |             |\n       |             |\nxD, yD |-------------| xC, yC\n");
        System.out.println("xA: ");
        tempX = sc.nextInt();
        System.out.println("yA: ");
        tempY = sc.nextInt();
        System.out.println("xB: ");
        tempX2 = sc.nextInt();
        System.out.println("yC");
        tempY2 = sc.nextInt();
        A = new points(tempX, tempY);
        B = new points(tempX2, tempY);
        C = new points(tempX2, tempY2);
        D = new points(tempX, tempY2);
    }

    public void rotateImg(image editImg) {
        System.out.println("Choose angle to rotate (90, 180, 270): ");
        int angle = sc.nextInt(); // we read the angle
        double radians = Math.toRadians(angle); // we turn it into radians

        BufferedImage original = editImg.getImage(); //<---- OG
        int width = editImg.getWidth();
        int height = editImg.getHeight();

        // First we create a copy to read original pixel data while we modify the main image
        BufferedImage copy = new BufferedImage(width, height, original.getType()); //<---- Copy   ((we use getType() because it provides BufferedImage with the image's type)
        for(int y=0; y<height; y++) {
            for(int x=0; x<width; x++) {
                copy.setRGB(x, y, original.getRGB(x, y));
            }
        }

        lambda calc = new lambda(); // we create a lambda object to calculate the 2 barycentric coordinates
        double centerX = (A.getx() + C.getx()) / 2.0; //We calculate the center
        double centerY = (A.gety() + C.gety()) / 2.0;

        // Then we clear the original rectangle area (painting it black) before drawing the rotated one
        for(int y=0; y<height; y++) {
            for(int x=0; x<width; x++) {
                if(calc.isInside(x, y, A, B, C) || calc.isInside(x, y, A, C, D)) {
                    original.setRGB(x, y, 0xFF000000);
                }
            }
        }

        // Lastly we iterate over the *ENTIRE* image to paint the rotated content, not on the copy
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double relX = x - centerX;
                double relY = y - centerY;

                // This part does the real magic, by using sine and cosine we find the "opposite" pixel which we are altering
                // by doing this, we can effectively rotate the image counter-clockwise instead of clock-wise,
                // it's like applying an inverse rotation matrix so we can go backwards. truly the most challenging part of the project, super cool tbh
                int srcX = (int) Math.round(relX * Math.cos(radians) + relY * Math.sin(radians) + centerX);
                int srcY = (int) Math.round(-relX * Math.sin(radians) + relY * Math.cos(radians) + centerY);

                // If the source pixel was inside the selection, we paint it at the current (x, y)
                if (srcX >= 0 && srcX < width && srcY >= 0 && srcY < height) {
                    if (calc.isInside(srcX, srcY, A, B, C) || calc.isInside(srcX, srcY, A, C, D)) {
                        original.setRGB(x, y, copy.getRGB(srcX, srcY));
                    }
                }
            }
        }

        // Save progress to editOutput.png
        try {
            File output = new File("src/editOutput.png");
            ImageIO.write(original, "png", output);
            System.out.println("Rotation applied and saved to editOutput.png");
        } catch (IOException e) {
            System.out.println("Error saving the image: " + e.getMessage());
        }
    }
}