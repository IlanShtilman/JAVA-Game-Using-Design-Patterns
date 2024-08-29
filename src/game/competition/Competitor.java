package game.competition;
import game.entities.IMobileEntity;
import java.awt.Color;

public interface Competitor extends IMobileEntity {
    public void initRace();

    //Getters
    void setColor(Color color);
    Color getColor();

    //Setters
    int getNumber();
    void setNumber(int number);

}

