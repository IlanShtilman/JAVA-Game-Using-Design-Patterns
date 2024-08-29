package game.competition;
import game.arena.WinterArena;
import game.entities.sportsman.SnowBorder;
import game.enums.Discipline;
import game.enums.Gender;
import game.enums.League;

public class SnowboardCompetition extends WinterCompetition {

     public SnowboardCompetition(WinterArena ar, int MaxPlys, Discipline ds, League league, Gender g){
        super(ar,MaxPlys,ds,league,g);
    }
    @Override
    public boolean isValidCompetitor(Competitor competitor) {
        if (!(competitor instanceof SnowBorder)) {
            return false;
        }
        return super.isValidCompetitor(competitor);
    }
    @Override
    public String toString() {
        return "SnowboardCompetition{" +
                "discipline=" + this.getDiscipline() +
                ", league=" + this.getLeague() +
                ", gender=" + this.getGender() +
                ", activeCompetitors=" + activeCompetitors +
                ", finishedCompetitors=" + finishedCompetitors +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SnowboardCompetition)) return false;
        if (!super.equals(obj)) return false;
        SnowboardCompetition that = (SnowboardCompetition) obj;
        return this.getDiscipline() == that.getDiscipline() &&
                this.getLeague() == that.getLeague() &&
                this.getGender() == that.getGender() &&
                activeCompetitors.equals(that.activeCompetitors) &&
                finishedCompetitors.equals(that.finishedCompetitors);
    }
}

