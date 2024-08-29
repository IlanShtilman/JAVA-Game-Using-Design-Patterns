package game.States;

public class FinishedState implements MobileAlertState {

    private final long Finishtime;

    public FinishedState(){
        this.Finishtime = System.currentTimeMillis();
    }

    @Override
    public void updateStatus(AlertStateContext context, CompetitionStatus status) {
        status.updateCompetitorStatus(context.getCompetitorID(),"Finished",Finishtime);
    }

    @Override
    public void checkStateChange(AlertStateContext context) {
        //Same as Disabled, there no changes since we're done with the specific competitor
    }
    //Getters
    public long getFinishedtime() {return Finishtime;}
}