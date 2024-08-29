package game.arena;

import game.enums.SnowSurface;
import game.enums.WeatherCondition;

//Since we are not using the Summer at our game, we will throw Exception (That was for Using Factory-method only for WinterArena)
public class SummerArenaFactory implements ArenaFactory {
    @Override
    public IArena createArena(double length, SnowSurface surface, WeatherCondition condition) {
        throw new UnsupportedOperationException("Summer Arena is not supported for ski/snowboard Competition competitions");
    }
}