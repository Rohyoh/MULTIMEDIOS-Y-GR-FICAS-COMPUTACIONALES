public class PointLight extends Light {
    private Vector3D position;

    public PointLight(Vector3D position, int[] color, double intensity) {
        super(color, intensity);
        this.position = position;
    }

    @Override
    public Vector3D getDirection(Vector3D hitPoint) {
        // Vector from hitPoint TO the light
        return Vector3D.L(position, hitPoint).normalize();
    }
}