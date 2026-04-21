public class Intersection {
    private double distance;
    private Object3D object;

    public Intersection(double distance, Object3D object) {
        this.distance = distance;
        this.object = object;
    }

    public double getDistance() { return distance; }
    public Object3D getObject() { return object; }
}