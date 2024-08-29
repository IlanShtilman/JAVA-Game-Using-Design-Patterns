package game.entities;

import utilities.Point;

public abstract class Entity {

   private Point location;
    //default Constructor
    public Entity() {
        this.location = new Point(0, 0);
    }
    //Constructor ,we define the new location
    public Entity(Point location){
        this.location = new Point(location);
    }
    //Copy Constructor
    public Entity(Entity other) {
        this(other.location);
    }
    //set the location for the competitor
    public void setLocation(Point location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        this.location = new Point(location);
    }
    //Function tostring
    @Override
    public String toString() {
        return "Entity{location=" + location + '}';
    }
    //equals , we're comparing the objects and checking if Entity is the type
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Entity)) return false;
        Entity entity = (Entity) other;
        return location.equals(entity.location);
    }
    //Getter
    public Point getLocation(){return this.location;}
}