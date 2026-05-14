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

        OBJreader cube = new OBJreader(
                "objects/cube.obj",           // obj's path
                new int[]{200, 200, 200},     // Color (orange)
                20,                         // Scale
                0,                           // translateX
                -25,                        // translateY
                -4.5                          // translateZ
        );
        scene.addObject(cube.getObj());

        /*OBJreader reader = new OBJreader(
                "objects/starv2.obj",           // obj's path
                new int[]{255, 100, 0},     // Color (orange)
                0.5,                         // Scale
                0,                           // translateX
                -0.5,                        // translateY
                -3                          // translateZ
        );
        scene.addObject(reader.getObj());*/

        /*OBJreader reader2 = new OBJreader(
                "objects/temple.obj",           // obj's path
                new int[]{255, 100, 0},     // Color
                0.05,                         // Scale
                0,                           // translateX
                -0.5,                        // translateY
                -2                          // translateZ
        );
        scene.addObject(reader2.getObj());*/

        // Create lights
        // Directional light 1
        /*scene.addLight(new DirectionalLight(
                new Vector3D(0, 1, 1),
                new int[]{255, 255, 255},
                1.0
        ));*/

        // Directional light 2
        /*scene.addLight(new DirectionalLight(
                new Vector3D(0, 2, 2),
                new int[]{255, 0, 0},
                1.0
        ));*/

        // Point light
        scene.addLight(new PointLight(
                new Vector3D(0, 2, -2),
                new int[]{255, 255, 255},      // slightly bluish
                3.0
        ));

        // Create camera
        // Camera is at origin (0, 0, 0) looking towards -Z
        Camera camera = new Camera(
                new Vector3D(0, 0, 0),       // Position at origin
                75,                           // field of view
                width,
                height,
                0.1,        // nearplane
                2000         // farplane
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
    private static BufferedImage render(Scene scene, Camera camera, int width, int height) {
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
                    Vector3D normal = intersection.getNormal(); // already flat or interpolated (Phong)

                    // Calculate hit point O + t*D
                    Vector3D hitPoint = ray.getCamera().add(
                            ray.getDirection().multiply(intersection.getDistance())
                    );

                    int[] objColor = hitObject.getColor();

                    // Accumulate lighting from all lights
                    double r = 0, g = 0, b = 0;
                    for (Light light : scene.getLights()) {
                        Vector3D lightDir = light.getDirection(hitPoint);

                        // Lambertian diffuse: max(0, N·L)
                        double angle = Math.max(0, normal.point(lightDir));

                        // Skip if surface faces away from light
                        if (angle <= 0) continue;

                        // SHADOW RAY: check if there's an obstruction between hitPoint and light
                        // We offset the origin slightly along the normal to avoid self-shadowing
                        Vector3D shadowRayOrigin = hitPoint.add(normal.multiply(0.001));
                        Ray shadowRay = new Ray(shadowRayOrigin, lightDir);

                        // Calculate maximum distance to check for shadows
                        double maxShadowDistance;
                        if (light instanceof PointLight) {
                            // For point lights, only check up to the light position
                            Vector3D toLight = Vector3D.L(((PointLight) light).getPosition(), hitPoint);
                            maxShadowDistance = Math.sqrt(toLight.square());
                        } else {
                            // For directional lights, check up to far plane
                            maxShadowDistance = camera.getFar();
                        }

                        // Check for shadow collision
                        Intersection shadowHit = scene.raycast(shadowRay, 0.001, maxShadowDistance);

                        // If there's an obstruction, skip this light (we're in shadow)
                        if (shadowHit != null) continue;

                        // Calculate light intensity with falloff for PointLight
                        double Li;
                        if (light instanceof PointLight) {
                            // Apply inverse square law: L_I = intensity / d²
                            Li = ((PointLight) light).getIntensityAtPoint(hitPoint);
                        } else {
                            // DirectionalLight has no falloff (sun is infinitely far)
                            Li = light.getIntensity();
                        }

                        // Lc * Oc * Li * angle
                        int[] Lc = light.getColor();
                        r += (Lc[0] / 255.0) * (objColor[0] / 255.0) * Li * angle * 255;
                        g += (Lc[1] / 255.0) * (objColor[1] / 255.0) * Li * angle * 255;
                        b += (Lc[2] / 255.0) * (objColor[2] / 255.0) * Li * angle * 255;
                    }

                    // Clamp to [0, 255]
                    int rr = (int) Math.min(r, 255);
                    int gg = (int) Math.min(g, 255);
                    int bb = (int) Math.min(b, 255);

                    rgb = (rr << 16) | (gg << 8) | bb;
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