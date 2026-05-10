import java.util.ArrayList;
import java.util.List;

public class Scene {
    private List<Object3D> objects;
    private List<Light> lights; // BOMBOCLAT! now we support multiple lights

    public Scene() {
        this.objects = new ArrayList<>();
        this.lights = new ArrayList<>();
    }

    // Add an object to the scene
    public void addObject(Object3D object) {
        this.objects.add(object);
    }

    // Add a light to the scene
    public void addLight(Light light) {
        this.lights.add(light);
    }

    // Find the closest intersection with any object in the scene
    // Returns null if no intersection found
    public Intersection raycast(Ray ray, double near, double far) {
        Intersection closestIntersection = null;

        // We search through each object in the scene
        for (Object3D obj : objects) {
            Intersection intersection = obj.collition(ray);

            if (intersection != null) {
                double dist = intersection.getDistance();
                // The object has to be between near & far plane
                if (dist >= near && dist <= far) {
                    if (closestIntersection == null || dist < closestIntersection.getDistance()) {
                        closestIntersection = intersection;
                    }
                }
            }
        }
        return closestIntersection;
    }

    public List<Object3D> getObjects() {
        return objects;
    }

    public List<Light> getLights() {
        return lights;
    }
}