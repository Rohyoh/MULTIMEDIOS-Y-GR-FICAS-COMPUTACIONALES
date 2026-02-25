public class lambda {
    public lambda() {}

    // Method to check if a point (px, py) is inside a triangle (p1, p2, p3)
    public boolean isInside(int px, int py, points p1, points p2, points p3) {
        // Barycentric coordinate formula
        double denominator = (double) ((p2.gety() - p3.gety()) * (p1.getx() - p3.getx()) + (p3.getx() - p2.getx()) * (p1.gety() - p3.gety()));

        double l1 = ((p2.gety() - p3.gety()) * (px - p3.getx()) + (p3.getx() - p2.getx()) * (py - p3.gety())) / denominator;
        double l2 = ((p3.gety() - p1.gety()) * (px - p3.getx()) + (p1.getx() - p3.getx()) * (py - p3.gety())) / denominator;
        double l3 = 1.0 - l1 - l2;

        // Calculates if all lambdas are between 0 and 1, the point is inside
        return l1 >= 0 && l1 <= 1 && l2 >= 0 && l2 <= 1 && l3 >= 0 && l3 <= 1;
    }
}