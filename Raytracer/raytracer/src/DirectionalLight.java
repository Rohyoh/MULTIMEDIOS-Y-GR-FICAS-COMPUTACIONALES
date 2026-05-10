public class DirectionalLight extends Light {
    private Vector3D direction;

    public DirectionalLight(Vector3D direction, int[] color, double intensity) {
        super(color, intensity);
        this.direction = direction.normalize(); // we store it already normalized
    }

    @Override
    public Vector3D getDirection(Vector3D hitPoint) {
        return direction; // constant direction, no matter the hit point
    }
}