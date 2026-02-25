import java.awt.image.BufferedImage;
import java.util.Scanner;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class invertColors {
    private points A, B, C, D; //we store the 4 pairs of points that we will rotate
    int tempX = 0, tempX2=0, tempY = 0, tempY2=0; //temporal variables to create a bounding box in which the operation will be made
    Scanner sc = new Scanner(System.in);

    public invertColors() {
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
        System.out.println("yC: ");
        tempY2 = sc.nextInt();
        A = new points(tempX, tempY);
        B = new points(tempX2, tempY);
        C = new points(tempX2, tempY2);
        D = new points(tempX, tempY2);
    }

    public void paintImg(image editImg) {
        BufferedImage img = editImg.getImage();
        if (img == null) return;

        lambda calc = new lambda();

        // We iterate through every pixel of the image
        for (int y = 0; y < editImg.getHeight(); y++) {
            for (int x = 0; x < editImg.getWidth(); x++) {

                // Check if the pixel is inside Triangle 1 (ABC) or Triangle 2 (ADC), (theres an image in the documentation document that visually represents it)
                if (calc.isInside(x, y, A, B, C) || calc.isInside(x, y, A, C, D)) {
                    int rgb = img.getRGB(x, y);

                    // Extracting colors
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = rgb & 0xFF;

                    // Inverting colors
                    r = 255 - r;
                    g = 255 - g;
                    b = 255 - b;

                    // Setting the new color
                    int newRgb = (0xFF << 24) | (r << 16) | (g << 8) | b; //ARGB notation
                    img.setRGB(x, y, newRgb);
                }
            }
        }

        // Save progress to editOutput.png
        try {
            File output = new File("src/editOutput.png");
            ImageIO.write(img, "png", output);
            System.out.println("Image saved as editOutput.png");
        } catch (IOException e) {
            System.out.println("Error saving the image: " + e.getMessage());
        }
    }
}