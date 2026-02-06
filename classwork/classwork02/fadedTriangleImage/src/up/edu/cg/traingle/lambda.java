package up.edu.cg.traingle;

public class lambda { // This class calculates the value of the 3 lambdas
    int xA = 100, xB = 400, xC = 250; // The x coordinate for A,B and C
    int yA = 150, yB = 150, yC = 400; // The y coordinate
    public float calculateLambda(int ticket, int xCord, int yCord) {
        float lambdaValue = 0;
        switch (ticket) { // The ticket determines the lambda to evaluate
            case 1:
                float lambda1 = (float)((yB-yC)*(xCord-xC)+(xC-xB)*(yCord-yC))/((yB-yC)*(xA-xC)+(xC-xB)*(yA-yC)); // We use the Barycentric formula
                lambdaValue = lambda1; //We calculate the first lambda value
                break;
            case 2:
                float lambda2 = (float)((yC-yA)*(xCord-xC)+(xA-xC)*(yCord-yC))/((yB-yC)*(xA-xC)+(xC-xB)*(yA-yC));
                lambdaValue = lambda2; //We calculate the second lambda value
                break;
        } //We calculate the third lambda value elsewhere, it's just a substraction
        return lambdaValue;
    }
}
