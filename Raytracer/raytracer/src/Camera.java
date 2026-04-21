public class Camera {
    private Vector3D position;    // Camera position
    private double fov;           // Field of view in degrees
    private double aspectRatio;   // width / height ratio

    // X axis points right, Y axis points up, Z axis points backwards
    public Camera(Vector3D position, double fov, int width, int height) {
        this.position = position;
        this.fov = fov;
        this.aspectRatio = (double) width / height;
    }

    // Generate a ray for pixel (x, y)
    // Camera coordinate system:
    //   - Right: +X axis
    //   - Up: +Y axis
    //   - Forward: -Z axis (into the screen)
    public Ray generateRay(int x, int y, int width, int height) {
        // Normalize pixel coordinates to [-1, 1] range
        // (0, 0) is top-left corner
        // Center of image is (0, 0) when the coordinates are normalized
        double px = (2.0 * (x + 0.5) / width - 1.0) * aspectRatio;
        double py = 1.0 - 2.0 * (y + 0.5) / height;

        // Apply field of view
        // Larger FOV = wider view angle
        double fovRad = Math.toRadians(fov);
        double tanFov = Math.tan(fovRad / 2.0);
        px *= tanFov;
        py *= tanFov;

        // Ray direction in frustum
        // Camera looks towards -Z, so direction is (px, py, -1)
        // We normalize to get a unit direction vector (u <-- in calculus 3)
        Vector3D direction = new Vector3D(px, py, -1).normalize();

        return new Ray(position, direction);
    }

    public Vector3D getPosition() {
        return position;
    }
}