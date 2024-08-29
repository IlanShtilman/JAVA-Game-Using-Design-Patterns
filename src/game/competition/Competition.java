package game.competition;
import game.States.AlertStateContext;
import game.States.CompetitionStatus;
import game.States.FinishedState;
import game.arena.IArena;
import game.entities.IMobileEntity;
import game.entities.sportsman.WinterSportsman;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

//Our class for handle the competition. We're using here the Observe-Observable DesignPattern and implement the CS interface, for Updating the State (Pattern)
public abstract class Competition implements PropertyChangeListener, CompetitionStatus {
    protected IArena arena;
    protected int maxCompetitors;
    protected List<Competitor> activeCompetitors;
    protected ArrayList<Competitor> finishedCompetitors;
    protected List<AlertStateContext> competitorStates;
    protected boolean competitionEnded;

    public Competition(IArena ar, int MaxPlys) {
        setArena(ar);
        setMaxcompetitors(MaxPlys);
        this.activeCompetitors = new CopyOnWriteArrayList<>();
        this.finishedCompetitors = new ArrayList<>();
        this.competitorStates = new ArrayList<>();
        this.competitionEnded = false;
    }

    public abstract boolean isValidCompetitor(Competitor competitor);

    public void addCompetitor(Competitor competitor) throws IllegalArgumentException {
        if (competitor instanceof WinterSportsman) {
            if (activeCompetitors.size() >= maxCompetitors) {
                throw new IllegalStateException("Maximum number of competitors reached");
            }
            activeCompetitors.add(competitor);
            ((WinterSportsman) competitor).addPropertyChangeListener(this);

            // Create and add a new AlertStateContext for this competitor
            AlertStateContext stateContext = new AlertStateContext(competitor.getNumber());
            competitorStates.add(stateContext);
        } else {
            throw new IllegalArgumentException("Competitor must be a WinterSportsman");
        }
    }

    //Our Function that starting our race, updating all the states - ActiveState + sending them with Thread. while we didn't reach the length/threads working we continue
    public void startCompetition() {
        for (int i = 0; i < activeCompetitors.size(); i++) {
            Competitor competitor = activeCompetitors.get(i);
            AlertStateContext stateContext = competitorStates.get(i);

            if (competitor instanceof WinterSportsman) {
                new Thread(() -> {
                    WinterSportsman sportsman = (WinterSportsman) competitor;
                    while (!Thread.currentThread().isInterrupted() && !arena.isFinished(sportsman)) {
                        sportsman.move(arena.getFriction());
                        stateContext.updateStatus(this);
                        stateContext.checkStateChange();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    finishCompetitor(sportsman);
                }).start();
            }
        }
    }

    //Checking if their any competitor at Active/Finished competitors arrays . when activeCompetitors will be empty that means the race ended. synchronized to make sure 2 competitors didn't reach that part of the code together
    public synchronized void finishCompetitor(Competitor competitor) {
        if (activeCompetitors.contains(competitor) && arena.isFinished((IMobileEntity) competitor)) {
            activeCompetitors.remove(competitor);
            finishedCompetitors.add(competitor);

            //Updating the competitor to "Finished" State
            AlertStateContext stateContext = getStateContextForCompetitor(competitor);
            if (stateContext != null) {
                stateContext.setState(new FinishedState());
                updateCompetitorStatus(competitor.getNumber(), "Finished", System.currentTimeMillis());
            }

            System.out.println(competitor.getClass().getName() + " has finished the race.");
        }

        if (activeCompetitors.isEmpty()) {
            competitionEnded = true;
            endCompetition();
        }
    }

    //Function that get the specific Competitor based on the ID
    public AlertStateContext getStateContextForCompetitor(Competitor competitor) {
        return competitorStates.stream()
                .filter(context -> context.getCompetitorID() == competitor.getNumber())
                .findFirst()
                .orElse(null);
    }

    //Sending a "Signal" - kind of Notify with our threads involved - to know at which state the competitor is
    @Override
    public void updateCompetitorStatus(int competitorId, String status, long time) {

        System.out.println("Competitor " + competitorId + " status updated to " + status + " at time " + time);
    }


    protected void endCompetition() {
        competitionEnded = true;

        // Only finish active competitors who have actually crossed the finish line
        for (Competitor competitor : new ArrayList<>(activeCompetitors)) {
            if (arena.isFinished((IMobileEntity) competitor)) {
                finishCompetitor(competitor);
            }
        }

        System.out.println("Competition ended!");
    }

    //Our Function of Observe-Observable , we Override the function to make the Competition notice if any there any changes with our Competitors
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("location".equals(evt.getPropertyName())) {
            WinterSportsman sportsman = (WinterSportsman) evt.getSource();
            synchronized (this) {
                if (arena.isFinished(sportsman)) {
                    finishCompetitor(sportsman);
                } else {
                    AlertStateContext stateContext = getStateContextForCompetitor(sportsman);
                    if (stateContext != null) {
                        stateContext.checkStateChange();
                        stateContext.updateStatus(this);
                    }
                }
            }
        }
    }

    //Function that comparing the different competitors and then updating based on that their rank at place
     public int getCurrentRating(WinterSportsman sportsman) {
        List<Competitor> sortedCompetitors = new ArrayList<>(activeCompetitors);
        sortedCompetitors.sort((c1, c2) -> Double.compare(
            ((WinterSportsman)c2).getLocation().getX(),
            ((WinterSportsman)c1).getLocation().getX()
        ));
        return sortedCompetitors.indexOf(sportsman) + 1;
    }


    //We're doing loop over our competitors, and checking if we're done with their movements
    public void playTurn() {
        int moves = 0;
        while (hasActiveCompetitors()) {
            Iterator<Competitor> iterator = activeCompetitors.iterator();
            while (iterator.hasNext()) {
                Competitor competitor = iterator.next();
                competitor.move(arena.getFriction());
                //Here we check with isFinished if the competitor done
                if(arena.isFinished(competitor)){
                    finishedCompetitors.add(competitor);
                    System.out.println("Added!");
                    iterator.remove();
                }
            }
            moves++;
        }
        System.out.println("Race Finished in : " + moves + " Steps");
    }

    //Method to check if there is any comp left
    public boolean hasActiveCompetitors() {
        return !activeCompetitors.isEmpty();
    }

    //Function that returning the list of Competitors States
    public List<AlertStateContext> getCompetitorStates() {
        return competitorStates;
    }

    //Set function - while creating the competition
    public void setArena(IArena arena) {
        if (arena == null) {
            throw new IllegalArgumentException("Arena cannot be null");
        }
        this.arena = arena;
    }
    public void setMaxcompetitors(int max){
        if (max < 0) {
            throw new IllegalArgumentException("Number of players cannot be negative");
        }
        this.maxCompetitors = max;
    }

    public int getMaxCompetitors(){return this.maxCompetitors;}
    public IArena getArena(){return this.arena;}

    public ArrayList<Competitor> getFinishedCompetitors(){return this.finishedCompetitors;}
    public List<Competitor> getActiveCompetitors(){return this.activeCompetitors;}
    @Override
    public String toString() {
        return "Competition{" +
                "arena=" + arena +
                ", maxCompetitors=" + maxCompetitors +
                ", activeCompetitors=" + activeCompetitors +
                ", finishedCompetitors=" + finishedCompetitors +
                '}';
    }
    @Override
    //equals , we're comparing the objects and checking if competition is the type
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Competition that = (Competition) obj;

        return arena.equals(that.arena)
                && maxCompetitors == that.maxCompetitors
                && activeCompetitors.equals(that.activeCompetitors)
                && finishedCompetitors.equals(that.finishedCompetitors);
    }
}





