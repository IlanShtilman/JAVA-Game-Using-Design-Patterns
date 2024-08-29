package game.States;

public class ActiveState implements MobileAlertState {

    //A function that update the competitor using interface "Competition Status"
    @Override
    public void updateStatus(AlertStateContext context, CompetitionStatus status) {
        status.updateCompetitorStatus(context.getCompetitorID(),"Active",System.currentTimeMillis());
    }

    //A function that keeping the same State/Change it to injured
    @Override
    public void checkStateChange(AlertStateContext context) {
        if(Math.random()<0.05){
            context.setState(new InjuredState());
        }
    }
}

