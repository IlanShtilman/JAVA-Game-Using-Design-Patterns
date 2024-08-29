package game.States;

//Our class for State Pattern - we have here 2 Interfaces and 4 classes we will use.
//CompetitionStatus will be usable at Competition to let the arena and competition know about the states changing
// MobileAlertState and the other 4 state class will be those who will be changed dew to changes we make at our states

public class AlertStateContext {

    private MobileAlertState CurrentState;
    private int CompetitorID;

    //Constructor of our Alert (Define it as Active at start)
    public AlertStateContext(int ID){
        this.CompetitorID = ID;
        CurrentState = new ActiveState();
    }
    //Function for setting the new State
    public void setState(MobileAlertState NewState){ this.CurrentState = NewState;}

    //Getters
    public int getCompetitorID(){return this.CompetitorID;}
    public MobileAlertState getState(){return this.CurrentState;}

    // Delegate updateStatus to the current state
    public void updateStatus(CompetitionStatus status) {
        CurrentState.updateStatus(this, status);
    }

    // Delegate checkStateChange to the current state
    public void checkStateChange() {
        CurrentState.checkStateChange(this);
    }

}
