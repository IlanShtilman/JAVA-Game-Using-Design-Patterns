package game.States;

//An interface will be for us to let us with AlertStateContext to check the current state and change it
public interface MobileAlertState {
    void updateStatus(AlertStateContext context, CompetitionStatus status);
    void checkStateChange(AlertStateContext context);
}
