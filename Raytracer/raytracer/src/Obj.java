import java.util.ArrayList;
import java.util.List;

public class Obj extends Object3D {
    private ArrayList<Triangle> triangles;
    private int[] color;
    private Bvhleaf triangleBVH;   // Internal BVH over triangles

    public Obj(ArrayList<Double> Vertices, ArrayList<Integer> id,
               ArrayList<Double> vNormals, ArrayList<Integer> idNormals,
               ArrayList<Integer> faceSmoothGroups, int[] color) {
        super(new Vector3D(0, 0, 0)); // Relative position with the origin
        this.color = color;
        this.triangles = new ArrayList<>();
        objBuilder(Vertices, id, vNormals, idNormals, faceSmoothGroups);
        buildTriangleBVH();   // Build BVH after constructing all triangles
    }

    public void objBuilder(ArrayList<Double> Vertices, ArrayList<Integer> id,
                           ArrayList<Double> vNormals, ArrayList<Integer> idNormals,
                           ArrayList<Integer> faceSmoothGroups) {
        int numVertices = Vertices.size() / 3;

        int faceIndex = 0;
        for(int i = 0; i + 2 < id.size(); i += 3, faceIndex++) {
            int vertexIndex1 = id.get(i) - 1;
            int vertexIndex2 = id.get(i + 1) - 1;
            int vertexIndex3 = id.get(i + 2) - 1;

            if (vertexIndex1 < 0 || vertexIndex1 >= numVertices ||
                    vertexIndex2 < 0 || vertexIndex2 >= numVertices ||
                    vertexIndex3 < 0 || vertexIndex3 >= numVertices) {
                System.err.println("BOMBOCLAT!: Invalid vertex index at face " + faceIndex);
                continue;
            }

            int idx1 = vertexIndex1 * 3;
            int idx2 = vertexIndex2 * 3;
            int idx3 = vertexIndex3 * 3;

            Vector3D v1 = new Vector3D(
                    Vertices.get(idx1),
                    Vertices.get(idx1 + 1),
                    Vertices.get(idx1 + 2)
            );
            Vector3D v2 = new Vector3D(
                    Vertices.get(idx2),
                    Vertices.get(idx2 + 1),
                    Vertices.get(idx2 + 2)
            );
            Vector3D v3 = new Vector3D(
                    Vertices.get(idx3),
                    Vertices.get(idx3 + 1),
                    Vertices.get(idx3 + 2)
            );

            // Retrieve vertex normals if available
            Vector3D vn1 = null, vn2 = null, vn3 = null;
            if (vNormals != null && !vNormals.isEmpty() && idNormals != null) {
                int ni1 = idNormals.get(i) - 1;
                int ni2 = idNormals.get(i + 1) - 1;
                int ni3 = idNormals.get(i + 2) - 1;
                vn1 = new Vector3D(vNormals.get(ni1*3), vNormals.get(ni1*3+1), vNormals.get(ni1*3+2));
                vn2 = new Vector3D(vNormals.get(ni2*3), vNormals.get(ni2*3+1), vNormals.get(ni2*3+2));
                vn3 = new Vector3D(vNormals.get(ni3*3), vNormals.get(ni3*3+1), vNormals.get(ni3*3+2));
            }

            int smooth = (faceSmoothGroups != null && faceIndex < faceSmoothGroups.size())
                    ? faceSmoothGroups.get(faceIndex) : 0;

            Triangle t = new Triangle(v1, v2, v3, this.color, vn1, vn2, vn3, smooth);
            triangles.add(t);
        }

        // Calculate bounding box for the entire Obj
        this.boundingBox = null;
        for (Triangle triangle : triangles) {
            this.boundingBox = BoundingBox.merge(this.boundingBox, triangle.getBoundingBox());
        }
    }

    // Build internal BVH over triangles
    private void buildTriangleBVH() {
        if (triangles.isEmpty()) {
            triangleBVH = null;
        } else {
            // Convert triangles to List<Object3D> (Triangle extends Object3D)
            List<Object3D> triList = new ArrayList<>(triangles);
            triangleBVH = Bvhleaf.build(triList);
        }
    }

    @Override
    public Intersection collition(Ray ray) {
        // Early rejection using object's global bounding box
        if (this.boundingBox != null && !this.boundingBox.intersects(ray, 0.0001, Double.POSITIVE_INFINITY)) {
            return null;
        }

        // Use internal BVH if available
        if (triangleBVH != null) {
            Intersection hit = triangleBVH.intersect(ray, 0.0001, Double.POSITIVE_INFINITY);
            if (hit != null) {
                Triangle tri = (Triangle) hit.getObject();
                Vector3D finalNormal;
                if (tri.getSmoothGroup() == 0 || !tri.hasVertexNormals()) {
                    finalNormal = tri.getNormal();
                } else {
                    finalNormal = tri.getInterpolatedNormal(hit.getU(), hit.getV());
                }
                hit.setNormal(finalNormal);
            }
            return hit;
        } else {
            // Fallback to linear search
            Intersection closest = null;
            for (Triangle triangle : triangles) {
                Intersection hit = triangle.collition(ray);
                if (hit != null) {
                    if (closest == null || hit.getDistance() < closest.getDistance()) {
                        closest = hit;
                    }
                }
            }
            if (closest != null) {
                Triangle tri = (Triangle) closest.getObject();
                Vector3D finalNormal;
                if (tri.getSmoothGroup() == 0 || !tri.hasVertexNormals()) {
                    finalNormal = tri.getNormal();
                } else {
                    finalNormal = tri.getInterpolatedNormal(closest.getU(), closest.getV());
                }
                closest.setNormal(finalNormal);
            }
            return closest;
        }
    }

    @Override
    public int[] DiffuseShading(Vector3D rayDirection, int[] color, double rayIntensity, Vector3D normal) {
        return new int[]{0, 0, 0}; // Unused, shading handled per triangle
    }

    @Override
    public int[] getColor() {
        return color;
    }
}