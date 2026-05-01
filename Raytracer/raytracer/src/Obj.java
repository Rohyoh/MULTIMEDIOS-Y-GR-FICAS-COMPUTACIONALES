import java.util.ArrayList;

public class Obj extends Object3D {
    ArrayList<Triangle> triangles = new ArrayList<>();
    private int[] color; // Obj's color
    private ArrayList<Double> originalVertices; // We store the original vertices

    public Obj(ArrayList<Double> Vertices, ArrayList<Integer> id, int[] color) {
        // We use the first vertices as the obj's base
        super(new Vector3D(0, 0, 0)); // Relative position with the origin
        this.color = color;
        this.originalVertices = new ArrayList<>(Vertices); // Copy of the original vertices
        objBuilder(Vertices, id);
    }

    /* We use the following method to move the object around our scene    <-------- Redundant
    public void translate(double dx, double dy, double dz) {
        triangles.clear();
        ArrayList<Double> translatedVertices = new ArrayList<>();

        for (int i = 0; i < originalVertices.size(); i += 3) {
            translatedVertices.add(originalVertices.get(i) + dx);
            translatedVertices.add(originalVertices.get(i + 1) + dy);
            translatedVertices.add(originalVertices.get(i + 2) + dz);
        }

        // We reconstruct the triangles with the now moved vertices
        rebuildTriangles(translatedVertices);
    } */

    /* We scale the object in x, y, z       <---------- Redundant
    public void scale(double sx, double sy, double sz) {
        triangles.clear();
        ArrayList<Double> scaledVertices = new ArrayList<>();

        for (int i = 0; i < originalVertices.size(); i += 3) {
            scaledVertices.add(originalVertices.get(i) * sx);
            scaledVertices.add(originalVertices.get(i + 1) * sy);
            scaledVertices.add(originalVertices.get(i + 2) * sz);
        }
        // We reconstruct the triangles with the now scaled vertices
        rebuildTriangles(scaledVertices);
    }

    // we can also scale uniformly in x, y, z by applying the same constant to all vertices
    public void scale(double s) {
        scale(s, s, s);
    } */

    /* Receives moved/scaled vertices --> reconstructs all the triangles <------- redundant
    private void rebuildTriangles(ArrayList<Double> vertices) {
        // We do not need to read all the ids once more, because before calling this method the ids are read beforehand
        // ONLY uses previously processed vertices
        for (Triangle t : new ArrayList<>(triangles)) {
            // We create triangles --> we update the list
        }
    } */

    public void objBuilder(ArrayList<Double> Vertices, ArrayList<Integer> id){
        /*used for debugging <---------------

        System.out.println("Total vertices: " + Vertices.size() / 3);
        System.out.println("Total face indices: " + id.size());

         We can calculate the bounds of the object for debugging purposes <---------------

        double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;
        double minZ = Double.MAX_VALUE, maxZ = Double.MIN_VALUE;

        for (int i = 0; i < Vertices.size(); i += 3) {
            double x = Vertices.get(i);
            double y = Vertices.get(i + 1);
            double z = Vertices.get(i + 2);

            minX = Math.min(minX, x); maxX = Math.max(maxX, x);
            minY = Math.min(minY, y); maxY = Math.max(maxY, y);
            minZ = Math.min(minZ, z); maxZ = Math.max(maxZ, z);
        }

        System.out.println("Object bounds:");
        System.out.println("  X: [" + minX + ", " + maxX + "]");
        System.out.println("  Y: [" + minY + ", " + maxY + "]");
        System.out.println("  Z: [" + minZ + ", " + maxZ + "]");
        System.out.println("  Center: (" + ((minX+maxX)/2) + ", " + ((minY+maxY)/2) + ", " + ((minZ+maxZ)/2) + ")");*/

        // We build the triangles using the vertices ids & the vertices itself
        for(int i = 0; i + 2 < id.size(); i += 3){
            // let us remember that obj files begins on an index 1
            // Thus we subtract 1 to make the indexes begin at 1
            int vertexIndex1 = id.get(i) - 1;     // Index base 0 :)
            int vertexIndex2 = id.get(i + 1) - 1;
            int vertexIndex3 = id.get(i + 2) - 1;

            // We verify that none of the vertices are invalid
            int numVertices = Vertices.size() / 3;  // It has to be a multiple of 3... TRIANGLES
            if (vertexIndex1 < 0 || vertexIndex1 >= numVertices || vertexIndex2 < 0 || vertexIndex2 >= numVertices || vertexIndex3 < 0 || vertexIndex3 >= numVertices) {
                System.err.println("BOMBOCLAT!: Invalid vertex index at face " + (i/3)); //red color will make it obvious, isn't it?
                continue;
            }

            // We create indexes for each vertex (x,y,z) by multiplying by 3
            int idx1 = vertexIndex1 * 3;
            int idx2 = vertexIndex2 * 3;
            int idx3 = vertexIndex3 * 3;

            // We create the 3 triangle's vertices by storing each coordinate of them all
            Vector3D v1 = new Vector3D(
                    Vertices.get(idx1),     // x
                    Vertices.get(idx1 + 1), // y
                    Vertices.get(idx1 + 2)  // z
            );

            Vector3D v2 = new Vector3D(
                    Vertices.get(idx2),     // x
                    Vertices.get(idx2 + 1), // y
                    Vertices.get(idx2 + 2)  // z
            );

            Vector3D v3 = new Vector3D(
                    Vertices.get(idx3),     // x
                    Vertices.get(idx3 + 1), // y
                    Vertices.get(idx3 + 2)  // z
            );

            // We add the color to each of the triangles
            Triangle t = new Triangle(v1, v2, v3, this.color);

            // We add the triangle to the list
            triangles.add(t);
        }

        //Used for debugging the faces (triangles) created <-------------
        //System.out.println("Total triangles created: " + triangles.size());
    }

    @Override
    public Intersection collition(Ray ray) {
        Intersection closest = null;

        // We verify each obj's triangles intersection with each ray
        for (Triangle triangle : triangles) {
            Intersection hit = triangle.collition(ray);

            if (hit != null) { // if there's a hit
                if (closest == null || hit.getDistance() < closest.getDistance()) {
                    // We create a new intersection that points towards the obj, not towards that individual triangle
                    closest = new Intersection(hit.getDistance(), this);
                }
            }
        }

        return closest;
    }

    @Override
    public int[] getColor() {
        return color;
    }
}