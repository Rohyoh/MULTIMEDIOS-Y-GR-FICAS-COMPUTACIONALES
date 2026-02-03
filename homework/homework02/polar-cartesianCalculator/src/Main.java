import java.util.Scanner;

import up.edu.cg.calculator.calculator;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean flag = true;

        System.out.println("Welcome to the Polar Cartesian Calculator!");
        System.out.println("1) Polar --> Cartesian || 2) Cartesian --> Polar || 3) exit");


        do{
            int option = sc.nextInt();
            switch (option) {
                case 1:
                    calculator cartesianPolar = new up.edu.cg.calculator.calculator(1);
                    break;
                case 2:
                    calculator polarCartesian = new up.edu.cg.calculator.calculator(2);
                    break;
                case 3:
                    System.out.println("Exiting...");
                    flag = false;
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        } while (flag);

    }
}