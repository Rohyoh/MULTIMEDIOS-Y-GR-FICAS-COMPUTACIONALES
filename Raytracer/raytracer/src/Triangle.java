public class Triangle extends Object3D {
    private Vector3D v0, v1, v2, normal; // flat normal
    private Vector3D vn0, vn1, vn2;      // vertex normals (for Phong)
    private int smoothGroup;             // smoothing group
    private int[] color;

    // Original constructor (kept for compatibility)
    public Triangle(Vector3D v0, Vector3D v1, Vector3D v2, int[] color) {
        this(v0, v1, v2, color, null, null, null, 0);
    }

    // Full constructor with vertex normals and smooth group
    public Triangle(Vector3D v0, Vector3D v1, Vector3D v2, int[] color,
                    Vector3D vn0, Vector3D vn1, Vector3D vn2, int smoothGroup) {
        // We use the first vertex as the triangle's "base" or origin
        super(v0);
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
        this.vn0 = vn0;
        this.vn1 = vn1;
        this.vn2 = vn2;
        this.smoothGroup = smoothGroup;

        Vector3D V = Vector3D.L(v1, v0);
        Vector3D W = Vector3D.L(v2, v0);
        this.normal = Vector3D.cross(V, W);
        this.normal.normalize();
        this.color = color;
    }

    // Interpolate vertex normals using barycentric coordinates (u, v)
    // w = 1 - u - v  corresponds to v0, u to v1, v to v2
    public Vector3D getInterpolatedNormal(double u, double v) {
        if (!hasVertexNormals() || smoothGroup == 0) {
            return normal; // fallback to flat shading
        }
        double w = 1.0 - u - v;
        Vector3D n = new Vector3D(
                vn0.x * w + vn1.x * u + vn2.x * v,
                vn0.y * w + vn1.y * u + vn2.y * v,
                vn0.z * w + vn1.z * u + vn2.z * v
        );
        return n.normalize();
    }

    public boolean hasVertexNormals() {
        return vn0 != null && vn1 != null && vn2 != null;
    }

    public int getSmoothGroup() { return smoothGroup; }

    @Override
    public int[] DiffuseShading(Vector3D rayDirection, int[] color, double rayIntensity, Vector3D normal) {
        //We calculate the ray's direction... let us remember that cos(theta) = N (triangle's normal) dot L (rayDirection)
        // we use the normal passed as parameter -> important for Phong!
        double angle = Math.max(0, normal.point(rayDirection)); // by using max(0, etc) we're outting all possible negative values

        // We apply the lambertian surface formula for flat shading --> Lc * Oc * Li * Angle
        int r = (int)(color[0] * (this.color[0] / 255.0) * rayIntensity * angle);
        int g = (int)(color[1] * (this.color[1] / 255.0) * rayIntensity * angle);
        int b = (int)(color[2] * (this.color[2] / 255.0) * rayIntensity * angle);

        // We set the rgb values to 255
        return new int[]{Math.min(r, 255), Math.min(g, 255), Math.min(b, 255)};
    }

    @Override
    public Intersection collition(Ray ray) {
        // E1 = B - A
        Vector3D edge1 = Vector3D.L(v1, v0);
        // E2 = C - A
        Vector3D edge2 = Vector3D.L(v2, v0);

        // h = D x E2
        Vector3D h = Vector3D.cross(ray.getDirection(), edge2);
        // det = E1 · h
        double det = edge1.point(h);

        // if the det is close to 0 --> the ray is parallel to the triangle (edge case)
        if (det > -0.000001 && det < 0.000001) return null;

        double invDet = 1.0 / det;
        // s = O - A
        Vector3D s = Vector3D.L(ray.getCamera(), v0);

        // We calculate the coordinate "u"
        double u = invDet * s.point(h);
        if (u < 0.0 || u > 1.0) return null;

        // q = s x E1
        Vector3D q = Vector3D.cross(s, edge1);

        // We calculate the coordinate "v"
        double v = invDet * ray.getDirection().point(q);
        if (v < 0.0 || u + v > 1.0) return null;

        // We calculate t (the distance to the intersection)
        double t = invDet * edge2.point(q);

        // if t > 0 --> there's a valid intersection
        if (t > 0.000001) {
            Intersection hit = new Intersection(t, this);
            hit.setU(u);
            hit.setV(v);
            hit.setNormal(this.normal); // always provide at least the flat normal (fixes NullPointerException)
            return hit;
        }

        return null;
    }

    public int[] getColor() {
        return color;
    }
    public Vector3D getNormal() {return normal;}
}