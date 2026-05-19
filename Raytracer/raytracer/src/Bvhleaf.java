import java.util.ArrayList;
import java.util.List;

public class Bvhleaf {
    private BoundingBox boundingBox;     // AABB for this node
    private Bvhleaf left;                // Left child (null if leaf)
    private Bvhleaf right;               // Right child (null if leaf)
    private List<Object3D> objects;      // Objects in this node (only for leaves)

    // Constructor for leaf nodes
    public Bvhleaf(List<Object3D> objects) {
        this.objects = objects;
        this.left = null;
        this.right = null;

        // Calculate bounding box that contains all objects
        this.boundingBox = null;
        for (Object3D obj : objects) {
            this.boundingBox = BoundingBox.merge(this.boundingBox, obj.getBoundingBox());
        }
    }

    // Constructor for internal nodes
    public Bvhleaf(Bvhleaf left, Bvhleaf right) {
        this.left = left;
        this.right = right;
        this.objects = null;

        // Merge children's bounding boxes
        this.boundingBox = BoundingBox.merge(left.getBoundingBox(), right.getBoundingBox());
    }

    // we build the bvh from a list of objects
    public static Bvhleaf build(List<Object3D> objects) {
        if (objects == null || objects.isEmpty()) return null;
        // Copy list to avoid modifying original
        List<Object3D> copy = new ArrayList<>(objects);
        return buildRecursive(copy, 0);
    }

    private static Bvhleaf buildRecursive(List<Object3D> objects, int depth) {
        // Compute bounding box of all objects
        BoundingBox bbox = null;
        for (Object3D obj : objects) {
            bbox = BoundingBox.merge(bbox, obj.getBoundingBox());
        }

        // Leaf condition: small number of objects or depth limit
        final int LEAF_SIZE = 4;
        if (objects.size() <= LEAF_SIZE || depth > 20) {
            return new Bvhleaf(objects);
        }

        // Choose axis based on the longest extent
        Vector3D extent = new Vector3D(
                bbox.getMax().x - bbox.getMin().x,
                bbox.getMax().y - bbox.getMin().y,
                bbox.getMax().z - bbox.getMin().z
        );
        int axis = 0;
        if (extent.y > extent.x && extent.y > extent.z) axis = 1;
        else if (extent.z > extent.x && extent.z > extent.y) axis = 2;

        // Sort objects by centroid along the chosen axis
        final int splitAxis = axis;
        objects.sort((a, b) -> {
            double ca = getCentroidComponent(a.getBoundingBox(), splitAxis);
            double cb = getCentroidComponent(b.getBoundingBox(), splitAxis);
            return Double.compare(ca, cb);
        });

        // Median split
        int mid = objects.size() / 2;
        List<Object3D> leftObjs = new ArrayList<>(objects.subList(0, mid));
        List<Object3D> rightObjs = new ArrayList<>(objects.subList(mid, objects.size()));

        Bvhleaf left = buildRecursive(leftObjs, depth + 1);
        Bvhleaf right = buildRecursive(rightObjs, depth + 1);
        return new Bvhleaf(left, right);
    }

    private static double getCentroidComponent(BoundingBox bbox, int axis) {
        Vector3D c = bbox.getCenter();
        if (axis == 0) return c.x;
        if (axis == 1) return c.y;
        return c.z;
    }

    // Ray traversal logic
    public Intersection intersect(Ray ray, double tMin, double tMax) {
        if (!boundingBox.intersects(ray, tMin, tMax)) return null;

        if (isLeaf()) {
            Intersection closest = null;
            for (Object3D obj : objects) {
                Intersection hit = obj.collition(ray);
                if (hit != null) {
                    double t = hit.getDistance();
                    if (t >= tMin && t <= tMax) {
                        if (closest == null || t < closest.getDistance()) {
                            closest = hit;
                        }
                    }
                }
            }
            return closest;
        } else {
            // Traverse closer child first
            Bvhleaf first = left;
            Bvhleaf second = right;
            Vector3D origin = ray.getCamera();
            Vector3D dir = ray.getDirection();
            double tFirst = getEntryDistance(first.getBoundingBox(), origin, dir);
            double tSecond = getEntryDistance(second.getBoundingBox(), origin, dir);
            if (tFirst > tSecond) {
                Bvhleaf temp = first;
                first = second;
                second = temp;
            }

            Intersection hitFirst = first.intersect(ray, tMin, tMax);
            if (hitFirst != null) {
                double newTMax = hitFirst.getDistance();
                Intersection hitSecond = second.intersect(ray, tMin, newTMax);
                if (hitSecond != null && hitSecond.getDistance() < hitFirst.getDistance())
                    return hitSecond;
                return hitFirst;
            }
            return second.intersect(ray, tMin, tMax);
        }
    }

    private double getEntryDistance(BoundingBox bbox, Vector3D origin, Vector3D dir) {
        double tMin = Double.NEGATIVE_INFINITY;
        double tMax = Double.POSITIVE_INFINITY;
        for (int i = 0; i < 3; i++) {
            double invD = 1.0 / getComponent(dir, i);
            double t0 = (getComponent(bbox.getMin(), i) - getComponent(origin, i)) * invD;
            double t1 = (getComponent(bbox.getMax(), i) - getComponent(origin, i)) * invD;
            if (invD < 0.0) {
                double temp = t0; t0 = t1; t1 = temp;
            }
            tMin = Math.max(t0, tMin);
            tMax = Math.min(t1, tMax);
            if (tMax <= tMin) return Double.POSITIVE_INFINITY;
        }
        return tMin;
    }

    private double getComponent(Vector3D v, int index) {
        if (index == 0) return v.x;
        if (index == 1) return v.y;
        return v.z;
    }

    // getters
    public boolean isLeaf() {
        return objects != null;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public Bvhleaf getLeft() {
        return left;
    }

    public Bvhleaf getRight() {
        return right;
    }

    public List<Object3D> getObjects() {
        return objects;
    }
}