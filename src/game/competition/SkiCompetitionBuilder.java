package game.competition;
import game.arena.IArena;
import game.arena.WinterArena;
import game.arena.WinterArenaFactory;
import game.entities.sportsman.Skier;
import utilities.Point;
import java.awt.Color;

//Here we build the plan of Ski Competition. we will override the functions from CompetitionBuilder and make then using the plan all the data we need for setting the competition
public class SkiCompetitionBuilder implements CompetitionBuilder {

    private SkiCompetition competition;
    private SkiCompetitionPlan plan;

    //Setting the plan for our competition based on SkiCompetitionPlan
    @Override
    public void setCompetitionPlan(CompetitionPlan plan) {
        if (plan instanceof SkiCompetitionPlan) {
            this.plan = (SkiCompetitionPlan) plan;
        } else {
            throw new IllegalArgumentException("Invalid competition plan type");
        }
    }
    //Building the arena Based on factory-method
    @Override
    public void buildArena() {
        WinterArenaFactory factory = new WinterArenaFactory();
        IArena arena = factory.createArena(plan.getArenaLength(), plan.getSurface(), plan.getCondition());
        plan.setArena(arena);
    }

    //using the plan to get all the info we needed to our competition
    @Override
    public void buildCompetitionDetails() {
        competition = new SkiCompetition(
            (WinterArena)plan.getArena(),
            plan.getMaxCompetitors(),
            plan.getDiscipline(),
            plan.getLeague(),
            plan.getGender()
        );
    }

    @Override
    public void buildCompetitors(int numCompetitors) {
    if (competition == null) {
        throw new IllegalStateException("Competition must be initialized before adding competitors");
    }
    Skier prototype = new Skier("Prototype",
                                plan.getLeague().getLowerAge(),
                                plan.getGender(),
                                5,
                                20,
                                plan.getDiscipline());
    for (int i = 0; i < numCompetitors; i++) {
        try {
            Skier competitor = prototype.clone();
            competitor.setName("Skier" + (i + 1));
            double age = plan.getLeague().getLowerAge() +
                         (Math.random() * (plan.getLeague().getUpperAge() - plan.getLeague().getLowerAge()));
            competitor.setAge(age);
            competitor.setColor(new Color(Color.WHITE.getRGB()));
            competitor.setLocation(new Point(0, 0));
            competition.addCompetitor(competitor);
        } catch (CloneNotSupportedException e) {
            System.err.println("Error cloning competitor: " + e.getMessage());
        }
    }
}

    @Override
    public Competition getCompetition() {
        if (competition == null) {
            throw new IllegalStateException("Competition has not been built yet");
        }
        return competition;
    }
}