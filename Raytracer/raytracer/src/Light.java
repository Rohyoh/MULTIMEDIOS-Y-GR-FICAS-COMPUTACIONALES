public abstract class Light {
    protected int[] color;    //Lc
    protected double intensity;   //Li

    public Light(int[] color, double intensity) {
        this.color = color;
        this.intensity = intensity;
    }

    // Returns the normalized direction FROM the hit point TOWARDS the light
    public abstract Vector3D getDirection(Vector3D hitPoint);

    public int[] getColor() {
        return color;
    }

    public double getIntensity() {
        return intensity;
    }
}