package game.arena;

import game.enums.SnowSurface;
import game.enums.WeatherCondition;

//An interface that helping us the implement the Factory-method DesignPattern
public interface ArenaFactory {
    //Contains Only 1 function - create our arena
    IArena createArena(double length, SnowSurface surface, WeatherCondition condition);
}