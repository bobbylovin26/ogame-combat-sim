package ogame;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexey Tokar (azazeltap@yandex-team.ru)
 */
class User {
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

    public Ship get( int idx ) {
        return fleet.get( idx );
    }
}
