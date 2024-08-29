package game.entities.sportsman;

import game.enums.League;
import game.enums.Discipline;
import game.enums.Gender;
import game.competition.Competitor;
import utilities.Point;
import java.awt.*;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

    //Our class that implement the Decorator and Threads running, we based on our info from sportsman and entity
    public abstract class WinterSportsman extends Sportsman implements Competitor, Runnable, IWinterSportsman{
    private Discipline discipline;
    protected PropertyChangeSupport support;
    private double arenaLength;
    private Color color  = Color.WHITE;
    private static int nextId = 1;
    private int number;
    private int order;


    public WinterSportsman(String name, double age, Gender g, double acce, double maxS, Discipline dis) {
        super(maxS, acce, name, age, g);
        double bonus = League.calcAccelerationBonus(this.getAge());
        this.number = nextId++;
        this.setAcceleration(this.getAcceleration() + bonus);
        if (dis == null) {
            throw new IllegalArgumentException("Discipline cannot be empty");
        }
        this.discipline = dis;
        this.color = Color.WHITE; //Default Color
        this.support = new PropertyChangeSupport(this);
    }

    public Discipline getDiscipline() {
        return this.discipline;
    }

    public Color getColor(){return this.color;}

    //Function to set the color, we're using here also the Observer-Observable
    public void setColor(Color color) {
        Color oldColor = this.color;
        this.color = color;
        support.firePropertyChange("color", oldColor, color);
    }
    public void setAcceleration(double acc) {
        super.setAcceleration(acc);
    }

    public void setDiscipline(Discipline dis) {
        if (dis == null) {
            throw new IllegalArgumentException("Discipline cannot be empty");
        }
        this.discipline = dis;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }

    public void setArenaLength(double length) {
        this.arenaLength = length;
    }

    //Our way to clone sportsman and copy them , we make for each of them a unique ID
    @Override
     public WinterSportsman clone() throws CloneNotSupportedException {
      WinterSportsman cloned = (WinterSportsman) super.clone();
      cloned.number = nextId++;
      return cloned;
    }

    @Override
    public int getNumber() {
        return number;
    }

    public double getMaxSpeed(){return super.getMaxSpeed();};

    @Override
    public void setNumber(int number) {
        int oldNumber = this.number;
        this.number = number;
        support.firePropertyChange("number", oldNumber, number);
    }

    @Override
    public void initRace() {
        Point oldLocation = getLocation();
        setLocation(new Point(0, oldLocation.getY())); // Reset X to 0, keep Y the same
        support.firePropertyChange("location", oldLocation, getLocation());
    }

    //Our function for moving our player, we override the function from the interface
    @Override
    public void move(double friction) {
    double oldX = getLocation().getX();
    double newX = oldX + (getSpeed() * (1 - friction));
    newX = Math.min(newX, arenaLength); // Ensure we don't exceed arena length
    setLocation(new Point(newX, getLocation().getY()));

    // Update speed
    setSpeed(Math.min(getSpeed() + getAcceleration(), getMaxSpeed()));
    }

    //Threds - Start and then run() - here we make the move with turning on the threads
    @Override
    public void run() {
    while (!Thread.currentThread().isInterrupted() && getLocation().getX() < arenaLength) {
        move(0.01);  // Using a small friction value
        try {
            Thread.sleep(100); // Update more frequently
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
    }
    // Ensure the competitor finishes exactly at the arena length
        setLocation(new Point(arenaLength, getLocation().getY()));
}


    //2 function that we most to add for using the Observer and Observable
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    @Override
    public String toString() {
        return "WinterSportsman{Discipline='" + discipline + "', " + super.toString() + '}';
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof WinterSportsman)) return false;
        if (!super.equals(other)) return false;
        WinterSportsman sportsman = (WinterSportsman) other;
        return (sportsman.discipline == discipline && sportsman.number == number);
    }
}
