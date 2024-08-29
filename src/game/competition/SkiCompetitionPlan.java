package game.competition;
import game.arena.IArena;
import game.enums.*;

//A whole complete plan for Ski competition, All the fields for creating a proper SkiCompetition
public class SkiCompetitionPlan implements CompetitionPlan {
    private IArena arena;
    private Discipline discipline;
    private Gender gender;
    private League league;
    private SnowSurface surface;
    private WeatherCondition condition;
    private double arenaLength;
    private int maxCompetitors;

    public void setArena(IArena arena) {
        this.arena = arena;
    }

    public void setDiscipline(Discipline discipline) {
        this.discipline = discipline;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public void setSurface(SnowSurface surface) {
        this.surface = surface;
    }

    public void setCondition(WeatherCondition condition) {
        this.condition = condition;
    }

    public void setArenaLength(double length) {
        this.arenaLength = length;
    }

    public void setMaxCompetitors(int maxCompetitors) {
        this.maxCompetitors = maxCompetitors;
    }

    // Getters for all properties
    public IArena getArena() { return arena; }
    public Discipline getDiscipline() { return discipline; }
    public Gender getGender() { return gender; }
    public League getLeague() { return league; }
    public SnowSurface getSurface() { return surface; }
    public WeatherCondition getCondition() { return condition; }
    public double getArenaLength() { return arenaLength; }
    public int getMaxCompetitors() { return maxCompetitors; }
}