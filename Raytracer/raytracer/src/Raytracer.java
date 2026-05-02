import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Raytracer {
    public static void main(String[] args) {
        // Image dimensions
        int width = 800;
        int height = 600;

        // Create scene
        Scene scene = new Scene();

        // Add spheres to the scene
        // Sphere parameters: position, radius, color [R, G, B]
        // IMPORTANT NOTE: camera looks towards -Z, so --> -Z = front xd
        scene.addObject(new Sphere(
                new Vector3D(0, 0, -5),      // Center
                1.0,                          // Radius
                new int[]{255, 0, 0}         // Color (Red)
        ));

        scene.addObject(new Sphere(
                new Vector3D(2, 0, -6),      // Center
                0.5,                          // Radius
                new int[]{0, 0, 255}         // Color (blue)
        ));

        scene.addObject(new Triangle(
                new Vector3D(-2, -1, -5),     // Origin + the other 2 coordinates
                new Vector3D(2, -1, -5),
                new Vector3D(0, 2, -5),
                new int[]{0, 255, 0}         // Color (green)
        ));

        // Adds Obj to the scene
        OBJreader reader = new OBJreader(
                "objects/cat.obj",           // obj's path
                new int[]{255, 100, 0},     // Color (orange)
                0.009,                         // Scale
                0,                           // translateX
                -0.5,                        // translateY
                -4.5                           // translateZ
        );
        scene.addObject(reader.getObj());

        // Create camera
        // Camera is at origin (0, 0, 0) looking towards -Z
        Camera camera = new Camera(
                new Vector3D(0, 0, 0),       // Position at origin
                60,                           // field of view
                width,
                height,
                1.0,        // nearplane
                100         // farplane
        );

        // Render the scene
        System.out.println("Let him cook now...");
        BufferedImage image = render(scene, camera, width, height);

        // Save image to file
        try {
            ImageIO.write(image, "png", new File("output.png"));
            System.out.println("✓ Bomboclat image was succesfully stored as output.png <--- Poggers");
        } catch (IOException e) {
            System.err.println("✗ Bomboclat image failed to process, back to the coal mines:");
            e.printStackTrace();
        }
    }

    // rendering part
    // For each pixel, cast a ray and determine its color
    private static BufferedImage render(Scene scene, Camera camera,
                                        int width, int height) {
        // Create blank image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // For each pixel in the image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Generate ray for this pixel
                Ray ray = camera.generateRay(x, y, width, height);

                // We consider the near & far plane
                Intersection intersection = scene.raycast(ray, camera.getNear(), camera.getFar());

                int rgb;
                if (intersection != null) {
                    Object3D hitObject = intersection.getObject();
                    Vector3D normal = intersection.getNormal(); // Asegúrate de tener el getter en Intersection

                    // Light data
                    Vector3D Light = new Vector3D(0, 0, 5);
                    Light.normalize();
                    int[] Lc = {255, 255, 255}; // Light's color
                    double Li = 1.0;            // Max intensity

                    int[] color = hitObject.DiffuseShading(Light, Lc, Li, normal);
                    rgb = (color[0] << 16) | (color[1] << 8) | color[2];
                } else {
                    // Ray didn't hit anything --> background color (white)
                    rgb = 0xFFFFFF;
                }

                // Set pixel color
                image.setRGB(x, y, rgb);
            }

            // Progress indicator every 50 lines
            if (y % 50 == 0) {
                System.out.println("Progress: " + (y * 100 / height) + "%");
            }
        }

        System.out.println("Progress: 100%");
        return image;
    }
}