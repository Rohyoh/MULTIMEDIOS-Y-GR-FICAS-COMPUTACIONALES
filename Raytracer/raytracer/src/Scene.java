import java.util.ArrayList;
import java.util.List;

public class Scene {
    private List<Object3D> objects;

    public Scene() {
        this.objects = new ArrayList<>();
    }

    // Add an object to the scene
    public void addObject(Object3D object) {
        this.objects.add(object);
    }

    // Find the closest intersection with any object in the scene
    // Returns null if no intersection found
    public Intersection raycast(Ray ray) {
        Intersection closestIntersection = null;

        // We search through each object in the scene
        for (Object3D obj : objects) {
            Intersection intersection = obj.collition(ray);

            if (intersection != null) {
                // If this is the first intersection, or it's closer than previous
                if (closestIntersection == null || intersection.getDistance() < closestIntersection.getDistance()) {
                    closestIntersection = intersection;
                }
            }
        }

        return closestIntersection;
    }

    public List<Object3D> getObjects() {
        return objects;
    }
}