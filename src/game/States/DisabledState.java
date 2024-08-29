package game.States;

public class DisabledState implements MobileAlertState {

    @Override
    public void updateStatus(AlertStateContext context, CompetitionStatus status) {
        status.updateCompetitorStatus(context.getCompetitorID(),"Disabled",System.currentTimeMillis());
    }

    @Override
    public void checkStateChange(AlertStateContext context) {
    //there no changes since when we get that "Situation", we will no longer will use it
    }
}
