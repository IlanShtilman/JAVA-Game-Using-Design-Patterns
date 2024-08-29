package game.competition;

public class CompetitionDirector {
    private CompetitionBuilder builder;

    public CompetitionDirector(CompetitionBuilder builder) {
        this.builder = builder;
    }

    public void constructCompetition(CompetitionPlan plan, int numCompetitors) {
        builder.setCompetitionPlan(plan);
        builder.buildArena();
        builder.buildCompetitionDetails();
        builder.buildCompetitors(numCompetitors);
    }

    public Competition getCompetition() {
        return builder.getCompetition();
    }
}