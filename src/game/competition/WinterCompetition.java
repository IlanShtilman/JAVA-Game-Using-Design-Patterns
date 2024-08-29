package game.competition;
import game.arena.WinterArena;
import game.entities.sportsman.WinterSportsman;
import game.enums.Discipline;
import game.enums.Gender;
import game.enums.League;

public abstract class WinterCompetition extends Competition {

    private Discipline discipline;
    private League league;
    private Gender gender;

    public WinterCompetition(WinterArena ar, int MaxPlys, Discipline ds, League league, Gender g) {
         //Using the previous constructor
        super(ar, MaxPlys);
        setDisipline(ds);
        setLeague(league);
        this.gender = g;
    }
    //Setters
    public void setDisipline(Discipline ds) {
        if (ds == null) {
            throw new IllegalArgumentException("Discipline cannot be null");
        }
        this.discipline = ds;
    }

    public void setLeague(League league) {
        if (league == null) {
            throw new IllegalArgumentException("League cannot be null");
        }
        this.league = league;
    }
    //Here we're using isValidCompetitor , we write that and check about the rules for each competitors
    @Override
    public boolean isValidCompetitor(Competitor competitor) {
        if (!(competitor instanceof WinterSportsman)) {
            return false;
        }
        WinterSportsman winterSportsman = (WinterSportsman) competitor;
        return winterSportsman.getDiscipline() == this.discipline
                && winterSportsman.getGender() == this.gender
                && this.league.isInLeague(winterSportsman.getAge());
    }
     @Override
    public void startCompetition() {
        // You can add any winter-specific pre-competition setup here
        System.out.println("Starting Winter Competition: " + discipline + ", " + league + ", " + gender);
        super.startCompetition();
    }

     @Override
    public String toString() {
        return "WinterCompetition{" +
                "discipline=" + discipline +
                ", league=" + league +
                ", gender=" + gender +
                "} " + super.toString();
    }
    //equals , we're comparing the objects and checking if WinterCompetition is the type
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;

        WinterCompetition that = (WinterCompetition) obj;

        return discipline == that.discipline
                && league.equals(that.league)
                && gender == that.gender;
    }
    //Getters
    public Discipline getDiscipline(){return this.discipline;}
    public Gender getGender() { return this.gender; }
    public League getLeague(){return this.league;}
}
