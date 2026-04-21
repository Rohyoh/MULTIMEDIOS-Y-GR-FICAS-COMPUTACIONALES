public class Ray {
    private Vector3D camera, direction; // O & D

    public Ray(Vector3D camera, Vector3D direction) {
        this.camera = camera;
        this.direction = direction;
    }

    // get O
    public Vector3D getCamera() {
        return camera;
    }

    // get D
    public Vector3D getDirection() {
        return direction;
    }
}
