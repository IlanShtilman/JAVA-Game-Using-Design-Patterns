package game.entities.sportsman;

//Here our Winter Sportsman will get a change at his acceleration
public class SpeedySportsman extends WSDecorator {
    private static final double ACCELERATION_BOOST = 10;

    public SpeedySportsman(IWinterSportsman sportsman) {
        //Create the original winter sportsman and then apply the change
        super(sportsman);

        // Apply the speed boost immediately
        decoratedSportsman.setAcceleration(ACCELERATION_BOOST);
    }

    @Override
    public double getAcceleration() {
        // Return the boosted acceleration
        return decoratedSportsman.getAcceleration();
    }

    @Override
    public void setAcceleration(double acceleration) {
        // Set the boosted acceleration
        decoratedSportsman.setAcceleration(decoratedSportsman.getAcceleration() * acceleration);
    }
}