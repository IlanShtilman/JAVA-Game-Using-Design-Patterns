package game.arena;
import game.entities.IMobileEntity;

//An interface we're using for get our data about arena
public interface IArena {
    double getFriction();
    boolean isFinished(IMobileEntity me);
    double getLength();
}
