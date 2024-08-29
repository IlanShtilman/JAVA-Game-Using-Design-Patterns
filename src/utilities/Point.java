package utilities;

public class Point {
    private static final int MIN_X = 0;
    private static final int MAX_X = 1000000;
    private static final int MIN_Y = 0;
    private static final int MAX_Y = 800;

    private double x;
    private double y;

    //default constructor
    public Point(){
        this.x = 0;
        this.y = 0;
    }

    //constructor
    public Point(double x, double y) {
        if (x < MIN_X || x > MAX_X || y < MIN_Y || y > MAX_Y) {
            throw new IllegalArgumentException("Coordinates out of range");
        }
        this.x = x;
        this.y = y;
    }
    // copy constructor
    public Point(Point other) {
        this(other.x, other.y);
    }
    //Getters & Setters
    public double getX() { return this.x; }
    public double getY() { return this.y; }

    public void setX(double valueX) {
        if (valueX < MIN_X || valueX > MAX_X) {
            throw new IllegalArgumentException("X coordinate out of range");
        }
        this.x = valueX;
    }

    public void setY(double valueY) {
        if (valueY < MIN_Y || valueY > MAX_Y) {
            throw new IllegalArgumentException("Y coordinate out of range");
        }
        this.y = valueY;
    }
    //function equals and tostring for point class
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Point)) return false;
        Point point = (Point) other;
        return Double.compare(point.x, x) == 0 && Double.compare(point.y, y) == 0;
    }

    @Override
    public String toString() {
        return "Position{x=" + x + ", y=" + y + '}';
    }
}