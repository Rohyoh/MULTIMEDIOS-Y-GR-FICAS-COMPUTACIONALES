public class points {

    private int x, y; //all the 4 corners of our rectangle
    public points(int x, int y) {
        setPoints(x,y); //feeds x and y coordinates to setPoints() which will store the coords as a point
    }

    public void setPoints(int x, int y) {
        this.x = x; // we assign the coords
        this.y = y;
    }

    //A bunch of setters and getters XD
    public int getx() {
        return x;
    }
    public void setx(int x) {
        this.x = x;
    }
    public int gety() {
        return y;
    }
    public void sety(int y) {
        this.y = y;
    }
}
