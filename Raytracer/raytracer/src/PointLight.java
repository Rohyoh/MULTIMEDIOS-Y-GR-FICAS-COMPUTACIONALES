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

    // Calculate light falloff: L_I = intensity / d²
    public double getIntensityAtPoint(Vector3D hitPoint) {
        Vector3D toLight = Vector3D.L(position, hitPoint);
        double distance = Math.sqrt(toLight.square()); // d = ||L||

        // Avoid division by zero for very close points
        if (distance < 0.0001) distance = 0.0001;

        // Apply inverse square law: L_I = intensity / d²
        return intensity / (distance * distance);
    }

    public Vector3D getPosition() {
        return position;
    }
}