package game.entities.sportsman;

import game.enums.Discipline;
import game.enums.Gender;
import utilities.Point;
import java.awt.Color;

//Here we get at first an instance of Winter Sportsman, and then wrapped it with the decorator, we first need get all the "attrs" for our object
public abstract class WSDecorator implements IWinterSportsman {
    protected IWinterSportsman decoratedSportsman;

    public WSDecorator(IWinterSportsman sportsman) {
        this.decoratedSportsman = sportsman;
    }

    @Override
    public void move(double friction) {
        decoratedSportsman.move(friction);
    }

    public IWinterSportsman getDecoratedSportsman() {
    return decoratedSportsman;
    }

    @Override
    public Point getLocation() {
        return decoratedSportsman.getLocation();
    }

    @Override
    public double getMaxSpeed() {
        return decoratedSportsman.getMaxSpeed();
    }

    @Override
    public String getName() {
        return decoratedSportsman.getName();
    }

    @Override
    public double getAge() {
        return decoratedSportsman.getAge();
    }

    @Override
    public Gender getGender() {
        return decoratedSportsman.getGender();
    }

    @Override
    public Discipline getDiscipline() {
        return decoratedSportsman.getDiscipline();
    }

    @Override
    public double getAcceleration() {
        return decoratedSportsman.getAcceleration();
    }

    @Override
    public void setAcceleration(double acceleration) {
        decoratedSportsman.setAcceleration(acceleration);
    }

    @Override
    public Color getColor() {
        return decoratedSportsman.getColor();
    }

    @Override
    public void setColor(Color color) {
        decoratedSportsman.setColor(color);
    }
}
