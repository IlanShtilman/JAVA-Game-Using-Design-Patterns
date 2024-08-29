package game.arena;
import game.enums.SnowSurface;
import game.enums.WeatherCondition;
import game.entities.IMobileEntity;

//Class that implement the interface,we're here using enums for initialization most of our fields
public class WinterArena implements IArena {
    private double length;
    private final SnowSurface surface;
    private final WeatherCondition condition;
    //Constructor
    public WinterArena(double length, SnowSurface surface, WeatherCondition condition) {
         if (length <0) {
            throw new IllegalArgumentException("Length cannot be negative");
        }
        this.length = length;
        if (surface == null) {
            throw new IllegalArgumentException("Type Snow cannot be null");
        }
        this.surface = surface;
        if (condition == null) {
            throw new IllegalArgumentException("Weather Condition cannot be null");
        }
        this.condition = condition;
    }

    //Getting back the friction
    @Override
    public double getFriction() {
        return surface.getFriction();
    }
    //Checking if we cross the x point at the end
    @Override
    public boolean isFinished(IMobileEntity entity) {
        return entity.getLocation().getX() >= length;
    }
    @Override
    public double getLength() {
        return length;
    }
    public WeatherCondition getCondition(){return this.condition;}

}