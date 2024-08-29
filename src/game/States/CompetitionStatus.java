package game.States;

//An interface will be for us to let the competition know about the changes at our states
public interface CompetitionStatus {
    void updateCompetitorStatus(int competitorId, String status, long time);
}
