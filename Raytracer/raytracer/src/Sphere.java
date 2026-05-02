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

        // we use the valid t
        if (t0 > 0 || t1 > 0) {
            double t = (t0 > 0) ? t0 : t1;

            // We calculate the impact point (P = O + tD)
            Vector3D hitPoint = ray.getCamera().add(ray.getDirection().multiply(t));

            // We calculate the normal (P - center)
            Vector3D normal = Vector3D.L(hitPoint, this.getPosition());
            normal.normalize(); // ¡BOMBOCLAT IMPORTANT to normalize!

            // We create the intersection & pass the normal
            Intersection hit = new Intersection(t, this);
            hit.setNormal(normal);

            return hit;
        }
        return null;
    }

    @Override
    public int[] DiffuseShading(Vector3D rayDirection, int[] color, double rayIntensity, Vector3D normal) {
        //We calculate the ray's direction... let us remember that cos(theta) = N (triangle's normal) dot L (rayDirection)
        double angle = Math.max(0, normal.point(rayDirection)); // by using max(0, etc) we're outting all possible negative values

        // We apply the lambertian surface formula for flat shading --> Lc * Oc * Li * Angle
        int r = (int)(color[0] * (this.color[0] / 255.0) * rayIntensity * angle);
        int g = (int)(color[1] * (this.color[1] / 255.0) * rayIntensity * angle);
        int b = (int)(color[2] * (this.color[2] / 255.0) * rayIntensity * angle);

        // We set the rgb values to 255
        return new int[]{Math.min(r, 255), Math.min(g, 255), Math.min(b, 255)};
    }

    public int[] getColor() {
        return color;
    }
}