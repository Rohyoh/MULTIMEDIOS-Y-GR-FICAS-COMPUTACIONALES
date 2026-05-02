public class Intersection {
    private double distance;
    private Object3D object;
    private Vector3D normal;

    public Intersection(double distance, Object3D object) {
        this.distance = distance;
        this.object = object;
    }

    public void setNormal(Vector3D normal) { this.normal = normal;}
    public Vector3D getNormal() { return normal; }
    public double getDistance() { return distance; }
    public Object3D getObject() { return object; }
}