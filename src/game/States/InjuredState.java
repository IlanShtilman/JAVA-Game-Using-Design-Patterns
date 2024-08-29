package game.States;

public class InjuredState implements MobileAlertState{
    private long InjuryTime;

    public InjuredState(){
        this.InjuryTime = System.currentTimeMillis();
    }

    @Override
    public void updateStatus(AlertStateContext context, CompetitionStatus status) {
        status.updateCompetitorStatus(context.getCompetitorID(),"Injured",InjuryTime);
    }

    @Override
    public void checkStateChange(AlertStateContext context) {
        double ChancesBack = Math.random();
        if (ChancesBack < 0.3) {
            context.setState(new DisabledState());
        } else {
            context.setState(new ActiveState());
        }
    }

    public long getInjuryTime(){return this.InjuryTime;}
}
