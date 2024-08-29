package game.entities;
import utilities.Point;

public abstract class MobileEntity extends Entity implements IMobileEntity {
    private double maxSpeed;
    private double acceleration;
    private double speed;
    //constructor
    public MobileEntity(double maxS, double acce) {
        super();
        this.speed = 0;
        this.acceleration = acce;
        this.maxSpeed = maxS;
    }
    //Getters & Setters
    public double getMaxSpeed() { return this.maxSpeed; }
    public double getAcceleration() { return this.acceleration; }
    public double getSpeed() { return this.speed; }

    public void setMaxSpeed(double max) {
        if (max < 0) {
            throw new IllegalArgumentException("Max Speed cannot be negative");
        }
        this.maxSpeed = max;
    }

    public void setAcceleration(double acc) {
        if (acc < 0) {
            throw new IllegalArgumentException("Acceleration cannot be negative");
        }
        this.acceleration = acc;
    }

    public void setSpeed(double speed) {
        if (speed < 0) {
            throw new IllegalArgumentException("Speed cannot be negative");
        }
        this.speed = speed;
    }
    //Function move , the function using location,acceleration and speed to set again the X location
    public void move(double friction) {
        if (this.speed < this.maxSpeed)
            setSpeed(this.speed + this.acceleration * friction);
        if (this.speed > this.maxSpeed)
            setSpeed(this.maxSpeed);
        this.getLocation().setX(this.getLocation().getX() + speed);
    }

     //function tostring
    @Override
    public String toString() {
        return "MobileEntity{maxSpeed=" + maxSpeed + ", acceleration=" + acceleration + ", speed=" + speed + ", " + super.toString() + '}';
    }
    //equals , we're comparing the objects and checking if MobileEntity is the type
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof MobileEntity)) return false;
        if (!super.equals(other)) return false;
        MobileEntity that = (MobileEntity) other;
        return Double.compare(that.maxSpeed, maxSpeed) == 0 &&
               Double.compare(that.acceleration, acceleration) == 0 &&
               Double.compare(that.speed, speed) == 0;
    }
}