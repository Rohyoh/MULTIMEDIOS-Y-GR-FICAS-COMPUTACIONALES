package up.edu.cg.calculator;

import java.util.Scanner;

public class calculator {
    Scanner input = new Scanner(System.in);
    public calculator(int option) {
        if (option == 1) {
            polarCartesian();
        } else if (option == 2) {
            cartesianPolar();
        }
    }

    private void polarCartesian() {
        System.out.println("Provide the magnitude: ");
        double r = input.nextDouble();
        System.out.println("Provide the angle: ");
        double angle = input.nextDouble();
        double x =  Math.round(r * Math.cos(Math.toRadians(angle)));
        double y =  Math.round(r * Math.sin(Math.toRadians(angle)));
        System.out.println("("+x+","+y+")");
        System.out.println("Input an option ");
    }

    private void cartesianPolar() {
        System.out.println("Provide your x component: ");
        double x = input.nextInt();
        System.out.println("Provide your y component: ");
        double y = input.nextInt();
        double r = Math.sqrt((x * x) + (y * y));
        double angle = Math.toDegrees(Math.atan2(y, x));
        System.out.println("<" + angle +"°"+ ", " + r+">");
        System.out.println("Input an option ");
    }
}
