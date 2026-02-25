import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class image {
    private int w, h; //width and height variables
    private BufferedImage image;

    public image() throws IOException {
        try {
            // First, we try to load the edited version to keep progress
            File output = new File("src/editOutput.png");
            File input = new File("src/edit.png");
            BufferedImage img;

            if (output.exists()) {      //Validates if an output exists or not (in case that the original image is already edited)
                img = ImageIO.read(output);
            } else {
                img = ImageIO.read(input);
            }

            this.image = img;
            w = img.getWidth();     //stores width and height of the image
            h = img.getHeight();
        } catch (Exception e) {
            System.out.println("Error ---- 'edit.png' does not exist or its PATH is incorrect " + e.getMessage());
        }
    }

    public void setImage(BufferedImage img) {
        this.image = img;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setWidth(int w) { //This method sets our width
        this.w = w;
    }

    public void setHeight(int h) { //This method sets our height
        this.h = h;
    }
    public int getWidth() { //This method returns the width
        return w;
    }

    public int getHeight() { //This method returns the height
        return h;
    }

}
