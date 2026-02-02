package up.edu.cg.images;

import java.awt.*;
import java.lang.Math;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

public class Main {
    public static void main(String[] args) {
        BufferedImage image = new BufferedImage(600, 600, BufferedImage.TYPE_INT_RGB); //We create the canvas

        int radio = 250; //We state the circle's radio
        double x,y; //We declare but not initiate the x,y coordinates needed to calculate the circl's circunference points
        double hours = 10*30, minutes=50*6; //We declare the time, the right value being the degrees and the left one the said time;
        int markLength = 20; // the hour mark's lenght
        int centerX = 300, centerY = 300; //We state the center

        for (int a = 0; a < 360; a++) { // Circle cycle --- We iterate in each degree
        x=radio*Math.cos(Math.toRadians(a));//We obtain the x,y coordinates
        y=radio*Math.sin(Math.toRadians(a));

        int pxlX = centerX+(int)x; //We express where the coordinate is going to be shown on x,y respectively
        int pxlY = centerY+(int)y;
        image.setRGB(pxlX, pxlY, Color.white.getRGB()); //We graph the points

            for (int h = 0; h < 12; h++) {
                double angle = Math.toRadians(h * 30 - 90); // -90 to make 12 o'clock appear on top

                int outerX = centerX + (int)(radio * Math.cos(angle));// we calculate the marks with a difference
                int outerY = centerY + (int)(radio * Math.sin(angle));

                int innerX = centerX + (int)((radio - markLength) * Math.cos(angle));
                int innerY = centerY + (int)((radio - markLength) * Math.sin(angle));

                for (int i = 0; i <= markLength; i++) { // we draw the line
                    int markX = innerX + (outerX - innerX) * i/markLength; //i is the total distance while marklenght is the amount of segments to draw it... in this case, each mark consists of 25 points
                    int markY = innerY + (outerY - innerY) * i/markLength;

                    image.setRGB(markX, markY, Color.WHITE.getRGB());
                }
            }

        if (hours == a){ //hours cycle
            for(int j = 0; j < 200; j++){//we set the Hour hand lenght to 200 pxls
                int hourX = centerX+(int)(j*Math.cos(Math.toRadians(hours)));// the -90 is to ensure that the minutes are displayed correctly
                int hourY =  centerY+(int)(j*Math.sin(Math.toRadians(hours)));

                image.setRGB(hourX, hourY, Color.white.getRGB());
            }
        }

            if (minutes == a){ //minutes cycle
                for(int k = 0; k < 100; k++){//we set the Hour hand lenght to 100 pxls
                    int hourX = centerX+(int)(k*Math.cos(Math.toRadians(minutes-90)));// we do exactly the same we did to graph the circle, but the angle is constant while the magnitude isnt
                    int hourY =  centerY+(int)(k*Math.sin(Math.toRadians(minutes-90)));

                    image.setRGB(hourX, hourY, Color.white.getRGB());
                }
            }

    }

    File outputImage = new File("circle.png");// we create png the file
        try {
            ImageIO.write(image, "png", outputImage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}