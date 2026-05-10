import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.util.ArrayList;

public class OBJreader {
    public ArrayList<Double> vertices = new ArrayList<>(); // we use an arraylist rather than an array due to the fact that we don't know the real amount on vertices
    public ArrayList<Double> vNormals = new ArrayList<>();
    public ArrayList<Integer> id = new ArrayList<>();
    public ArrayList<Integer> idNormals = new ArrayList<>();
    public ArrayList<String> faceGroups = new ArrayList<>();
    public ArrayList<Integer> faceSmoothGroups = new ArrayList<>();
    private Obj obj; // we save the created object

    // Constructor with transformations
    public OBJreader(String filename, int[] color, double scale, double translateX, double translateY, double translateZ) {
        readObj(filename);
        generateMissingNormals(); // If no vn in file, get them by averaging face normals

        // We apply the corresponding transformations before building the object
        ArrayList<Double> transformedVertices = new ArrayList<>();
        for (int i = 0; i < vertices.size(); i += 3) {
            transformedVertices.add(vertices.get(i) * scale + translateX);
            transformedVertices.add(vertices.get(i + 1) * scale + translateY);
            transformedVertices.add(vertices.get(i + 2) * scale + translateZ);
        }

        obj = objBuilder(transformedVertices, id, vNormals, idNormals, faceSmoothGroups, color);
    }

    // we replaced verticesReader & instructionReader for an overall better method that does the work of them both while also adding new implementations
    public void readObj(String filename) {
        String currentGroup = "xd";           // if no "g" tag, all faces belong to a single group
        int currentSmoothGroup = 1;                // if no "s" tag, smoothing is on by default

        try (BufferedReader br = Files.newBufferedReader(Paths.get(filename))) { // io.BufferedReader has a simpler line control, way more convinient for this step
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue; // we skip empty lines & comments

                String[] parts = line.split("\\s+");

                if (line.startsWith("v ")) { // <--- lines with vertices
                    vertices.add(Double.parseDouble(parts[1])); // Takes the first vertex & stores it
                    vertices.add(Double.parseDouble(parts[2])); // Takes the second vertex & stores it
                    vertices.add(Double.parseDouble(parts[3])); // Takes the third vertex & stores it
                }
                else if (line.startsWith("vn ")) { // <--- lines with normals
                    vNormals.add(Double.parseDouble(parts[1])); // Takes the first normal & stores it
                    vNormals.add(Double.parseDouble(parts[2])); // Takes the second normal & stores it
                    vNormals.add(Double.parseDouble(parts[3])); // Takes the third normal & stores it
                }
                else if (line.startsWith("g ")) { // <--- lines that states groups
                    currentGroup = parts.length > 1 ? parts[1] : "xd"; // we store group names while sending to default incomplete groups such as "g "
                }
                else if (line.startsWith("s ")) { // <--- lines with smoothing
                    if (parts.length > 1) {
                        if (parts[1].equalsIgnoreCase("off")) { // in case of "off" we set smoothing to 0
                            currentSmoothGroup = 0;
                        } else {
                            try {
                                currentSmoothGroup = Integer.parseInt(parts[1]); // in case of an smooth value, we store it
                            } catch (NumberFormatException e) {
                                currentSmoothGroup = 1; // In case of an incorrect value, we set it to 1
                            }
                        }
                    }
                }
                else if (line.startsWith("f ")) { // <--- lines with faces
                    for (int i = 1; i < parts.length; i++) { // we begin at 1 because parts[0] = "f"
                        String[] ids = parts[i].split("/"); // We separate the block "1/11/1"  into -> ["1", "11",
                        if (ids.length > 0 && !ids[0].isEmpty()) {
                            id.add(Integer.parseInt(ids[0])); // we save the vertex id
                        }
                        if (ids.length > 2 && !ids[2].isEmpty()) {
                            idNormals.add(Integer.parseInt(ids[2])); // we save the normal id
                        }
                    }

                    faceGroups.add(currentGroup); // we store the face groups
                    faceSmoothGroups.add(currentSmoothGroup); // we store the smooth groups
                    // this was tuff ngl, tuff tuff tuff zahur.
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // if the OBJ file has no vertex normals, compute them by averaging the face normals
    private void generateMissingNormals() {
        if (!vNormals.isEmpty()) return; // normals already present

        int vertexCount = vertices.size() / 3;
        Vector3D[] accumulated = new Vector3D[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            accumulated[i] = new Vector3D(0, 0, 0);
        }

        // traverse all faces (assuming triangles: 3 indices per face)
        for (int i = 0; i + 2 < id.size(); i += 3) {
            int idx1 = id.get(i) - 1;
            int idx2 = id.get(i + 1) - 1;
            int idx3 = id.get(i + 2) - 1;

            Vector3D v1 = new Vector3D(
                    vertices.get(idx1 * 3), vertices.get(idx1 * 3 + 1), vertices.get(idx1 * 3 + 2));
            Vector3D v2 = new Vector3D(
                    vertices.get(idx2 * 3), vertices.get(idx2 * 3 + 1), vertices.get(idx2 * 3 + 2));
            Vector3D v3 = new Vector3D(
                    vertices.get(idx3 * 3), vertices.get(idx3 * 3 + 1), vertices.get(idx3 * 3 + 2));

            Vector3D faceNormal = Vector3D.cross(
                    Vector3D.L(v2, v1),
                    Vector3D.L(v3, v1)
            );

            accumulated[idx1] = accumulated[idx1].add(faceNormal);
            accumulated[idx2] = accumulated[idx2].add(faceNormal);
            accumulated[idx3] = accumulated[idx3].add(faceNormal);
        }

        // normalize and store
        for (int i = 0; i < vertexCount; i++) {
            Vector3D n = accumulated[i].normalize();
            vNormals.add(n.x);
            vNormals.add(n.y);
            vNormals.add(n.z);
        }

        // we fill idNormals so that each vertex uses its own normal
        idNormals.clear();
        idNormals.addAll(id);
    }

    public Obj objBuilder(ArrayList<Double> vertices, ArrayList<Integer> id,
                          ArrayList<Double> vNormals, ArrayList<Integer> idNormals,
                          ArrayList<Integer> faceSmoothGroups, int[] color) {
        Obj obj = new Obj(vertices, id, vNormals, idNormals, faceSmoothGroups, color); // we create the object with the gathered data
        return obj;
    }

    // Getter for the object
    public Obj getObj() {
        return obj;
    }
}