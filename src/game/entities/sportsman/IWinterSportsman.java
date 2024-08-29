
package game.entities.sportsman;
import game.entities.IMobileEntity;
import game.enums.Discipline;
import game.enums.Gender;
import java.awt.Color;

//Our interface our adding Decorator to WinterSportsman object
public interface IWinterSportsman extends IMobileEntity {
    String getName();
    double getAge();
    Gender getGender();
    Discipline getDiscipline();
    double getMaxSpeed();
    double getAcceleration();

    //Decorator
    void setAcceleration(double acceleration);
    Color getColor();
    void setColor(Color color);
}