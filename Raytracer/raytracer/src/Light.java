public class Light {
    private Vector3D direction;
    private int[] color;    //Lc
    private double intensity;   //Li

    public Light(Vector3D direction, int[] color, double intensity){
        this.direction = direction;
        direction.normalize();
        this.color = color;
        this.intensity = intensity;
    }

    public Vector3D getDirection(){return direction;}
    public int[] getColor(){return color;}
    public double getIntensity(){return intensity;}
}