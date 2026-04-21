class Vector3D {
    public double x, y, z;

    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // L = C - O
    // Calculates vector from point b to point a
    public static Vector3D L(Vector3D a, Vector3D b) {
        return new Vector3D(a.x - b.x, a.y - b.y, a.z - b.z);
    }
    
    // Used for: tca = L · D
    public double point(Vector3D v) {
        return (this.x * v.x) + (this.y * v.y) + (this.z * v.z);
    }

    // To calculate square values such as L^2 (we need its magnitude)
    // Returns the squared magnitude of the vector
    public double square(){
        return (this.x * this.x + this.y * this.y + this.z * this.z);
    }

    // We normalize our coordinates
    // returns a unit vector (magnitude = 1)
    public Vector3D normalize() {
        double mag = Math.sqrt(square());
        if (mag == 0) return this;
        return new Vector3D(x / mag, y / mag, z / mag);
    }

    // Vector addition
    // We return a new vector that is the sum of this + other
    public Vector3D add(Vector3D other) {
        return new Vector3D(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    // Scalar multiplication
    // We return a new vector scaled by scalar value
    public Vector3D multiply(double scalar) {
        return new Vector3D(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    // Cross product (vector product)
    // We use it for building orthogonal coordinate systems
    // Returns a vector perpendicular to both input vectors
    public static Vector3D cross(Vector3D a, Vector3D b) {
        return new Vector3D(
                a.y * b.z - a.z * b.y,
                a.z * b.x - a.x * b.z,
                a.x * b.y - a.y * b.x
        );
    }
}