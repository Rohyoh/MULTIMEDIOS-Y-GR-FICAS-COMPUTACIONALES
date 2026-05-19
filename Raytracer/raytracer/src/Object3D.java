abstract class Object3D {
    private Vector3D position;
    protected BoundingBox boundingBox; // AABB for optimization

    public Object3D(Vector3D position) {
        this.position = position;
        this.boundingBox = null; // We initialize in every subclass
    }

    public abstract int[] DiffuseShading(Vector3D rayDirection, int[] color, double rayIntensity, Vector3D normal); // overwritten by all objects

    abstract public Intersection collition(Ray ray); // overwritten by all objects

    abstract public int[] getColor(); // overwritten by all objects

    public Vector3D getPosition() {
        return position;
    }

    // Bounding box getter
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }
}