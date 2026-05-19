public class BoundingBox {
    private Vector3D min; // smallest corner (x_min, y_min, z_min)
    private Vector3D max; // biggest corner (x_max, y_max, z_max)

    public BoundingBox(Vector3D min, Vector3D max) {
        this.min = min;
        this.max = max;
    }

    // Create bounding boxes from a set of points
    public static BoundingBox fromPoints(Vector3D ... points) { // we let java packet all the arguments
        if (points.length == 0) return null;

        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;

        for (Vector3D p : points) {
            minX = Math.min(minX, p.x);
            minY = Math.min(minY, p.y);
            minZ = Math.min(minZ, p.z);
            maxX = Math.max(maxX, p.x);
            maxY = Math.max(maxY, p.y);
            maxZ = Math.max(maxZ, p.z);
        }

        return new BoundingBox( // max & min values create the bounding box
                new Vector3D(minX, minY, minZ),
                new Vector3D(maxX, maxY, maxZ)
        );
    }

    // Merge two bounding boxes (to create the hierarchy)
    public static BoundingBox merge(BoundingBox a, BoundingBox b) {
        if (a == null) return b;
        if (b == null) return a;

        return new BoundingBox(
                new Vector3D(
                        Math.min(a.min.x, b.min.x),
                        Math.min(a.min.y, b.min.y),
                        Math.min(a.min.z, b.min.z)
                ),
                new Vector3D(
                        Math.max(a.max.x, b.max.x),
                        Math.max(a.max.y, b.max.y),
                        Math.max(a.max.z, b.max.z)
                )
        );
    }

    // Fast ray-box intersection (slab method)
    // Returns true if ray intersects the box within [tMin, tMax] range
    public boolean intersects(Ray ray, double tMin, double tMax) {
        Vector3D origin = ray.getCamera();
        Vector3D direction = ray.getDirection();

        // For each axis (X, Y, Z), calculate intersection with the two slabs
        for (int i = 0; i < 3; i++) {
            double invD = 1.0 / getComponent(direction, i);
            double t0 = (getComponent(min, i) - getComponent(origin, i)) * invD;
            double t1 = (getComponent(max, i) - getComponent(origin, i)) * invD;

            // Swap if necessary
            if (invD < 0.0) {
                double temp = t0;
                t0 = t1;
                t1 = temp;
            }

            // Update tMin and tMax
            tMin = Math.max(t0, tMin);
            tMax = Math.min(t1, tMax);

            // Early rejection
            if (tMax < tMin) return false;
        }

        return true;
    }

    // small helper to get the components of a vector
    private double getComponent(Vector3D v, int index) {
        if (index == 0) return v.x;
        if (index == 1) return v.y;
        return v.z;
    }

    public Vector3D getMin() { return min; }
    public Vector3D getMax() { return max; }

    // We get the centroid for the bounding box
    public Vector3D getCenter() {
        return new Vector3D(
                (min.x + max.x) / 2.0,
                (min.y + max.y) / 2.0,
                (min.z + max.z) / 2.0
        );
    }

    // Failed attempt of using SAH instead of median split D:
    public double getSurfaceArea() {
        double dx = max.x - min.x;
        double dy = max.y - min.y;
        double dz = max.z - min.z;
        return 2.0 * (dx * dy + dy * dz + dz * dx);
    }
}