public class Sphere extends Object3D {
    private double radius;
    private int[] color; // RGB color [r, g, b]

    public Sphere(Vector3D position, double radius, int[] color) {
        super(position);
        this.radius = radius;
        this.color = color;
    }

    @Override
    public Intersection collition(Ray ray) {
        // L = C - O (vector from ray origin to sphere center)
        Vector3D Lvector = Vector3D.L(this.getPosition(), ray.getCamera());

        // tca = L · D (projection of L onto ray direction)
        double TCA = Lvector.point(ray.getDirection());

        // If tca < 0, sphere is behind the ray
        if (TCA < 0) return null;

        // d^2 = L^2 - tca^2 (perpendicular distance squared)
        double distanceC = Lvector.square() - TCA * TCA;
        double radiusSqr = this.radius * this.radius; // radius²

        // If d^2 > radius^2, ray misses the sphere
        if (distanceC > radiusSqr) return null;

        // thc = sqrt(radius^2 - d^2) (half-chord distance)
        double THC = Math.sqrt(radiusSqr - distanceC);

        // Calculate both intersection points
        double t0 = TCA - THC; // Entry point
        double t1 = TCA + THC; // Exit point

        // We decide which t to use
        double t;
        if (t0 > 0) {
            t = t0;  // We use the entry t0 (closest)
        } else if (t1 > 0) {
            t = t1;  // Camera inside the object, use t1
        } else {
            return null;  // Both behind the camera
        }

        // We create and return the intersection
        return new Intersection(t, this);
    }

    public int[] getColor() {
        return color;
    }
}