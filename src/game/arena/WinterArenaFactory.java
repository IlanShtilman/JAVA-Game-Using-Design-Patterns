package game.arena;

import game.enums.SnowSurface;
import game.enums.WeatherCondition;

//Factory-method only for WinterArena, we will create a new instance of Winter arena object
public class WinterArenaFactory implements ArenaFactory {
    @Override
    public IArena createArena(double length, SnowSurface surface, WeatherCondition condition) {
        return new WinterArena(length, surface, condition);
    }
}