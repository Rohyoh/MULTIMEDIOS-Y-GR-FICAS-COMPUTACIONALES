import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.ArrayList;

public class OBJreader {
    public ArrayList<Double> vertices = new ArrayList<>(); // we use an arraylist rather than an array due to the fact that we don't know the real amount on vertices
    public ArrayList<Integer> id = new ArrayList<>();
    private Obj obj; // we save the created object

    // Constructor with no transformation (the obj as it is)
    public OBJreader(String filename, int[] color) {
        verticesReader(filename); // we gather all the file's vertices
        instructionReader(filename); // we gather the instructions to build the object's faces
        obj = objBuilder(vertices, id, color);
    }

    // Constructor with transformations
    public OBJreader(String filename, int[] color, double scale, double translateX, double translateY, double translateZ) {
        verticesReader(filename);
        instructionReader(filename);

        // We apply the corresponding transformations before building the object
        ArrayList<Double> transformedVertices = new ArrayList<>();
        for (int i = 0; i < vertices.size(); i += 3) {
            transformedVertices.add(vertices.get(i) * scale + translateX);
            transformedVertices.add(vertices.get(i + 1) * scale + translateY);
            transformedVertices.add(vertices.get(i + 2) * scale + translateZ);
        }

        obj = objBuilder(transformedVertices, id, color);
    }

    public void verticesReader(String filename){
        try (Stream<String> string = Files.lines(Paths.get(filename))) {
            string.filter(l -> l.startsWith("v ")) // Only lines that begin with v (vertices)
                    .forEach(l->{
                        String[] coords = l.trim().split("\\s+");
                        if (coords.length >= 4) { // We verify the existence of the following components v x y z
                            double x = Double.parseDouble(coords[1]); // Takes the first vertex & stores it
                            vertices.add(x);
                            double y = Double.parseDouble(coords[2]); // Takes the second vertex & stores it
                            vertices.add(y);
                            double z = Double.parseDouble(coords[3]); // Takes the third vertex & stores it
                            vertices.add(z);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void instructionReader(String filename) {
        try (Stream<String> lines = Files.lines(Paths.get(filename))) {
            lines.filter(l -> l.trim().startsWith("f ")) // We take only the lines that begin with "f " (faces)
                    .forEach(l -> {
                        String[] parts = l.trim().split("\\s+");
                        // we begin at 1 because parts[0] = "f"
                        for (int i = 1; i < parts.length; i++) {
                            // We separate the block "1/11/1"  into -> ["1", "11", "1"]
                            // or simply "1" -> ["1"]
                            String[] ids = parts[i].split("/");
                            if (ids.length > 0 && !ids[0].isEmpty()) {
                                try {
                                    int vertexIndex = Integer.parseInt(ids[0]);
                                    id.add(vertexIndex); // We store the vertex id
                                } catch (NumberFormatException e) {
                                    System.err.println("BOMBOCLAT!: Could not parse vertex index: " + ids[0]);
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Obj objBuilder(ArrayList<Double> vertices, ArrayList<Integer> id, int[] color) {
        Obj obj = new Obj(vertices, id, color); // we create the object with the gathered data
        return obj;
    }

    // Getter for the object
    public Obj getObj() {
        return obj;
    }
}