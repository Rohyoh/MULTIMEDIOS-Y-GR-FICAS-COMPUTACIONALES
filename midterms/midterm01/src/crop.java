import java.awt.image.BufferedImage;
import java.util.Scanner;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class crop {
    private points A, B, C, D; //we store the 4 pairs of points that we will rotate
    int tempX = 0, tempX2=0, tempY = 0, tempY2=0, zero=0; //temporal variable
    Scanner sc = new Scanner(System.in);

    public crop() {
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

    public void cropImg(image editImg) {
        BufferedImage original = editImg.getImage();

        // Calculate width and height for the crop
        int newWidth = B.getx() - A.getx();
        int newHeight = C.gety() - A.gety();

        // Create the subimage (crop)
        BufferedImage cropped = original.getSubimage(A.getx(), A.gety(), newWidth, newHeight);

        // Update the image object with new data
        editImg.setImage(cropped);
        editImg.setWidth(newWidth);
        editImg.setHeight(newHeight);

        // Save progress to editOutput.png
        try {
            File output = new File("src/editOutput.png");
            ImageIO.write(cropped, "png", output);
            System.out.println("Image cropped and saved to editOutput.png");
        } catch (IOException e) {
            System.out.println("Error saving the crop: " + e.getMessage());
        }
    }
}