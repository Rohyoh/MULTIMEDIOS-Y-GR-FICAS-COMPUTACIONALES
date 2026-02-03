import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("width: "); //we ask the user for the width and height
        int w = sc.nextInt();
        System.out.print("height: ");
        int h = sc.nextInt();

        int a = w, b = h; //some auxiliary variables to calculate the aspect ratio
        while (b != 0) { //we will use Euclid's formula
            int temp = b; //we save temporally b
            b = a % b; // we store the residue on b
            a = temp; // we store b value on a
        }
        int gcd = a; //we store "a" in gcd variable (greatest common divisor)

        System.out.print("ratio: " + w/gcd + ":"+ h/gcd + " Decimal: "+ (double)w/h);// we print the result
        sc.close();
    }
}