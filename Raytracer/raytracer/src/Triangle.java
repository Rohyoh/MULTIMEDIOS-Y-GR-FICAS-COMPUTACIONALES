public class Triangle extends Object3D {
    private Vector3D v0, v1, v2;
    private int[] color;

    public Triangle(Vector3D v0, Vector3D v1, Vector3D v2, int[] color) {
        // We use the first vertex as the triangle's "base" or origin
        super(v0);
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
        this.color = color;
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
            return new Intersection(t, this);
        }

        return null;
    }

    public int[] getColor() {
        return color;
    }
}