package game.competition;
import game.arena.WinterArena;
import game.entities.sportsman.Skier;
import game.enums.Discipline;
import game.enums.Gender;
import game.enums.League;

    public class SkiCompetition extends WinterCompetition {
    public SkiCompetition(WinterArena ar, int MaxPlys, Discipline ds, League league, Gender g){
         //Using the previous constructor
        super(ar,MaxPlys,ds,league,g);
    }
    //Here we're checking if there is any valid competitor
    @Override
    public boolean isValidCompetitor(Competitor competitor) {
        if (!(competitor instanceof Skier)) {
            return false;
        }
        return super.isValidCompetitor(competitor);
    }
    @Override
    public String toString() {
        return "SkiCompetition{" +
                "discipline=" + this.getDiscipline() +
                ", league=" + this.getLeague() +
                ", gender=" + this.getGender() +
                ", activeCompetitors=" + activeCompetitors +
                ", finishedCompetitors=" + finishedCompetitors +
                '}';
    }
    //equals , we're comparing the objects and checking if SkiCompetition is the type
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SkiCompetition)) return false;
        if (!super.equals(obj)) return false;
        SkiCompetition that = (SkiCompetition) obj;
        return this.getDiscipline() == that.getDiscipline() &&
                this.getLeague() == that.getLeague() &&
                this.getGender() == that.getGender() &&
                activeCompetitors.equals(that.activeCompetitors) &&
                finishedCompetitors.equals(that.finishedCompetitors);
    }
}
