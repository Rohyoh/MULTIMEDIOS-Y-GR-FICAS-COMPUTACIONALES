import java.util.ArrayList;
import java.util.List;

public class Scene {
    private Bvhleaf bvhRoot;          // Root of the BVH
    private List<Object3D> objects;   // List of all objects (for dynamic updates)
    private List<Light> lights;
    private boolean bvhDirty;         // Flag to rebuild BVH when objects change

    public Scene() {
        this.objects = new ArrayList<>();
        this.lights = new ArrayList<>();
        this.bvhDirty = false;
    }

    // Add an object to the scene
    public void addObject(Object3D object) {
        this.objects.add(object);
        this.bvhDirty = true;   // Rebuild BVH on next raycast
    }

    // Add a light to the scene
    public void addLight(Light light) {
        this.lights.add(light);
    }

    // Rebuild BVH from current objects
    private void rebuildBVH() {
        if (objects.isEmpty()) {
            bvhRoot = null;
        } else {
            bvhRoot = Bvhleaf.build(objects);
        }
        bvhDirty = false;
    }

    // Find the closest intersection with any object in the scene
    // Returns null if no intersection found
    public Intersection raycast(Ray ray, double near, double far) {
        if (bvhDirty) rebuildBVH();
        if (bvhRoot == null) return null;
        return bvhRoot.intersect(ray, near, far);
    }

    public List<Light> getLights() {
        return lights;
    }

    public List<Object3D> getObjects() {
        return objects;
    }
}