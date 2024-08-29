package game.entities.sportsman;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import game.entities.MobileEntity;
import game.enums.Gender;
import utilities.Point;

//Our class with implement the cloneable will give us the options to make a clone sportsmans
public abstract class Sportsman extends MobileEntity implements Cloneable {
    private String name;
    private double age;
    private Gender gender;
    private PropertyChangeSupport changes;

    public Sportsman(double maxS, double acce, String name, double age, Gender G) {
        //Using the previous constructor
        super(maxS, acce);
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (age <= 0) {
            throw new IllegalArgumentException("Age must be positive");
        }
        if (G == null) {
            throw new IllegalArgumentException("Gender must be specified");
        }
        this.name = name;
        this.age = age;
        this.gender = G;
        this.changes = new PropertyChangeSupport(this);
    }

    @Override
    public Sportsman clone() throws CloneNotSupportedException {
        Sportsman cloned = (Sportsman) super.clone();
        cloned.changes = new PropertyChangeSupport(cloned);
        return cloned;
    }

    //Getters & Setters
    public String getName() { return this.name; }
    public double getAge() { return this.age; }
    public Gender getGender() { return this.gender; }

    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name;
    }

    public void setAge(double age) {
        if (age <= 0) {
            throw new IllegalArgumentException("Age must be positive");
        }
        this.age = age;
    }

    public void setGender(Gender gender) {
        if (gender == null) {
            throw new IllegalArgumentException("Gender must be specified");
        }
        this.gender = gender;
    }
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changes.removePropertyChangeListener(listener);
    }

    @Override
    public void setLocation(Point newLocation) {
        Point oldLocation = this.getLocation();
        super.setLocation(newLocation);
        changes.firePropertyChange("location", oldLocation, newLocation);
    }

    @Override
    public String toString() {
        return "Sportsman{name='" + name + "', age=" + age + ", gender=" + gender + ", " + super.toString() + '}';
    }
     //equals , we're comparing the objects and checking if Sportsman is the type
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Sportsman)) return false;
        if (!super.equals(other)) return false;
        Sportsman sportsman = (Sportsman) other;
        return Double.compare(sportsman.age, age) == 0 &&
               name.equals(sportsman.name) &&
               gender == sportsman.gender;
    }
}