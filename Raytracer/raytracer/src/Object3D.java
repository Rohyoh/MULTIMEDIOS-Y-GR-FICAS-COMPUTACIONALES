abstract class Object3D {
    private Vector3D position;
    public Object3D(Vector3D position) {
        this.position = position;
    }

    public abstract int[] DiffuseShading(Vector3D rayDirection, int[] color, double rayIntensity, Vector3D normal); // overwritten by all objects

    abstract public Intersection  collition(Ray ray); // overwritten by all objects

    abstract public int[] getColor(); // overwritten by all objects

    public Vector3D getPosition() {
        return position;
    }
}
