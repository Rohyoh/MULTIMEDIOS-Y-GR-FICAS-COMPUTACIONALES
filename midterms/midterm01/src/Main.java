import java.io.IOException;
import java.util.Scanner;

public class Main {
    static Scanner sc = new Scanner( System.in ); //Scanner to read user's inputs

    public static void main(String[] args) throws IOException {
        boolean token = true; //ticket that validates if the program is still running

        image editImg = new image(); //new image object which will contain the data of the image provided by the user

        System.out.println("||----- 1 ) Rotate || 2) Invert Color || 3) Crop || 4) Exit -----|"); // Program's menu
        while (token) { //Loop which contains all the program's options
            String ticket = sc.nextLine();
            switch (ticket){
                case "1": // this initiates the rotation of the image
                    rotate rotator = new rotate();
                    rotator.rotateImg(editImg);
                    break;
                case "2": // this initiates the inversion of colors of the image
                    invertColors inv = new invertColors();
                    inv.paintImg(editImg);
                    break;
                case "3": // this crops the image
                    crop cropper = new crop();
                    cropper.cropImg(editImg);
                    break;
                case "4": // this terminates the program and destroys the loop
                    token = false;
                    break;
            }
        }
    } //yessir :D

}