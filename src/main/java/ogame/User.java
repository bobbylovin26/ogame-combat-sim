package ogame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Alexey Tokar (azazeltap@yandex-team.ru)
 */
class User {
    private static final Random rnd = new Random();
    private final Researches researches;
    private final List<Ship> fleet = new ArrayList<Ship>();

    public User( Researches researches ) {
        this.researches = researches;
    }

    public void addFleet( Ship.Type type, int count ) {
        for ( int i = 0; i < count; i++ ) {
            fleet.add( new Ship( type ) );
        }
    }

    public Ship getTarget() {
        int idx = rnd.nextInt( fleet.size() );
        return fleet.get( idx );
    }

    public List<Ship> getFleet() {
        return fleet;
    }

    public void prepareForNextRound() {
        for ( Ship ship : fleet ) {
            ship.prepareForNextRound();
        }
    }

    public void calculateLosses() {
        for ( int shipIdx = fleet.size() - 1; shipIdx >= 0; shipIdx-- ) {
            if ( !fleet.get( shipIdx ).isSurvive() ) {
                fleet.remove( shipIdx );
            }
        }
    }

    public boolean isDefeated() {
        return fleet.isEmpty();
    }

    @Override
    public String toString() {
        return "User {" +
                "fleetSize=" + fleet.size() +
                '}';
    }
}
