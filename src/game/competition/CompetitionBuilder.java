package game.competition;

//Interface based on - Builder Pattern - were using it for build the default race
public interface CompetitionBuilder {
    void buildArena();
    void buildCompetitionDetails();
    void buildCompetitors(int numCompetitors);
    Competition getCompetition();
    void setCompetitionPlan(CompetitionPlan plan);
}