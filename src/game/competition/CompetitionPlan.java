package game.competition;
import game.arena.IArena;
import game.enums.Discipline;
import game.enums.Gender;

// Plan for Builder Pattern, most of the functions will be at SkiPlan
public interface CompetitionPlan {
    void setArena(IArena arena);
    void setDiscipline(Discipline discipline);
    void setGender(Gender gender);
}