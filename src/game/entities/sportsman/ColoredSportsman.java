package game.entities.sportsman;
import java.awt.Color;

//Here our Winter Sportsman will get a change at his color
public class ColoredSportsman extends WSDecorator {
    private Color color;

    public ColoredSportsman(IWinterSportsman sportsman, Color color) {
        super(sportsman);
        //Create the original winter sportsman and then apply the change
        this.setColor(color);
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
        decoratedSportsman.setColor(color);
    }
}