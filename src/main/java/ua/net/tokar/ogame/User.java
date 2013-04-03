package ua.net.tokar.ogame;

import com.google.common.collect.Maps;

import java.util.*;

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
            fleet.add( new Ship( type, researches ) );
        }
    }

    public List<Ship> getFleet() {
        return Collections.unmodifiableList( fleet );
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

    public void setFleet( Map<Ship.Type, Integer> fleet ) {
        this.fleet.clear();

        for ( Map.Entry<Ship.Type, Integer> e : fleet.entrySet() ) {
            addFleet( e.getKey(), e.getValue() );
        }
    }

    public Price getFleetCost() {
        Price cost = new Price( 0, 0, 0 );

        for ( Ship s : fleet ) {
            cost = cost.add( s.getType().price );
        }

        return cost;
    }

    public Map<Ship.Type, Integer> getShipsByType() {
        Map<Ship.Type, Integer> shipsByType = Maps.newHashMap();
        for ( Ship s : fleet ) {
            Integer cnt = shipsByType.get( s.getType() );
            shipsByType.put( s.getType(), ( cnt == null ? 1 : cnt + 1 ) );
        }

        return shipsByType;
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
