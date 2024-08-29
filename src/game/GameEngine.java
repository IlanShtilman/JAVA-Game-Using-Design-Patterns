package game;

import game.competition.Competition;

//Singleton class - we used that to run the game before moving to use the Gui class
public class GameEngine {

    private static GameEngine instance;

    // Private constructor to prevent instantiation outside the class
    protected GameEngine() {
    }

    // Method to get the singleton instance of GameEngine
    public static GameEngine getInstance() {
        if (instance == null) {
            instance = new GameEngine();
        }
        return instance;
    }

    // Method to start a competition
    public void startRace(Competition competition) {
        competition.playTurn();
        //System.out.println("Starting race for competition: " + competition);
        printResults(competition);
    }

    // Method to print results of a competition
    public void printResults(Competition competition) {
        System.out.println(competition.getFinishedCompetitors().size());
        for (int i=0;i<competition.getFinishedCompetitors().size();i++){
            System.out.println(i+1 + ". " + competition.getFinishedCompetitors().get(i));
        }
    }
}
