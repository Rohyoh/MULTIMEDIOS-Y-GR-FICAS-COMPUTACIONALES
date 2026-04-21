abstract class Object3D {
    private Vector3D position;
    public Object3D(Vector3D position) {
        this.position = position;
    }
    abstract public Intersection  collition(Ray ray);

    public Vector3D getPosition() {
        return position;
    }
}
