import java.util.ArrayList;

public class Obj extends Object3D {
    ArrayList<Triangle> triangles = new ArrayList<>();
    private int[] color; // Obj's color

    public Obj(ArrayList<Double> Vertices, ArrayList<Integer> id,
               ArrayList<Double> vNormals, ArrayList<Integer> idNormals,
               ArrayList<Integer> faceSmoothGroups, int[] color) {
        // We use the first vertices as the obj's base
        super(new Vector3D(0, 0, 0)); // Relative position with the origin
        this.color = color;
        objBuilder(Vertices, id, vNormals, idNormals, faceSmoothGroups);
    }

    public void objBuilder(ArrayList<Double> Vertices, ArrayList<Integer> id,
                           ArrayList<Double> vNormals, ArrayList<Integer> idNormals,
                           ArrayList<Integer> faceSmoothGroups) {
        int numVertices = Vertices.size() / 3;

        // For debugging
        /*
        System.out.println("Total vertices: " + numVertices);
        System.out.println("Total face indices: " + id.size());
        System.out.println("Total vNormals: " + (vNormals != null ? vNormals.size() / 3 : 0));
        System.out.println("Total idNormals: " + (idNormals != null ? idNormals.size() : 0));
        System.out.println("Total smooth groups: " + (faceSmoothGroups != null ? faceSmoothGroups.size() : 0));
        */

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

            // Create triangle with all data
            Triangle t = new Triangle(v1, v2, v3, this.color, vn1, vn2, vn3, smooth);
            triangles.add(t);
        }

        //System.out.println("Total triangles created: " + triangles.size());
    }

    @Override
    public Intersection collition(Ray ray) {
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
                finalNormal = tri.getNormal(); // flat shading
            } else {
                finalNormal = tri.getInterpolatedNormal(closest.getU(), closest.getV());
            }
            closest.setNormal(finalNormal);
        }

        return closest;
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