package game.entities.sportsman;
import game.enums.Discipline;
import game.enums.Gender;
import utilities.Point;
import java.awt.*;

public class Skier extends WinterSportsman {
    public Skier(String name,double age,Gender g,double acce, double maxS,Discipline dis){
      //Using the previous constructor
      super(name,age,g,acce,maxS,dis);
    }
    @Override
    public String toString() {
        return "Skier:" + super.toString();
    }
    //equals , we're comparing the objects and checking if Skier is the type
     @Override
     public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Skier)) return false;
        return super.equals(other);
    }
    @Override
    public Skier clone() throws CloneNotSupportedException {
        Skier cloned = (Skier) super.clone();
        cloned.setLocation(new Point(this.getLocation().getX(), this.getLocation().getY()));
        cloned.setColor(new Color(this.getColor().getRGB()));
        return cloned;
    }
}
