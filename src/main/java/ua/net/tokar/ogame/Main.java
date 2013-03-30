package ua.net.tokar.ogame;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class Main {
    private static final int MAX_ROUNDS = 6;
    private final static double STRUCTURE_INTO_DEBRIS_FACTOR = 0.3;

    public static void main( String[] args ) {
        User attacker = new User( new Researches( 8, 7, 8 ) );
        attacker.addFleet( Ship.Type.LF, 100 );

        User defender = new User( new Researches( 8, 7, 8 ) );
        defender.addFleet( Ship.Type.CRUISER, 5 );

        Map<Ship.Type, Integer> attackerShipsByTypeBeforeBattle = getShipsByType( attacker.getFleet() );
        Map<Ship.Type, Integer> defenderShipsByTypeBeforeBattle = getShipsByType( defender.getFleet() );

        simulate( attacker, defender );

        Map<Ship.Type, Integer> attackerShipsByTypeAfterBattle = getShipsByType( attacker.getFleet() );
        Map<Ship.Type, Integer> defenderShipsByTypeAfterBattle = getShipsByType( defender.getFleet() );

        Price attackerLoss = calculateLoss( attackerShipsByTypeBeforeBattle, attackerShipsByTypeAfterBattle );
        Price defenderLoss = calculateLoss( defenderShipsByTypeBeforeBattle, defenderShipsByTypeAfterBattle );

        System.out.println( "Attacker: " + attacker );
        System.out.println( "Defender: " + defender );

        System.out.println( "Attacker loss: " + attackerLoss );
        System.out.println( "Defender loss: " + defenderLoss );

        Price debris = attackerLoss.add( defenderLoss ).withoutDeuterium().mul( STRUCTURE_INTO_DEBRIS_FACTOR );

        System.out.println( "Debris: " + debris );
    }

    private static Price calculateLoss( Map<Ship.Type, Integer> shipsByTypeBefore, Map<Ship.Type, Integer> shipsByTypeAfter ) {
        Price gain = new Price( 0, 0, 0 );

        for ( Map.Entry<Ship.Type, Integer> e : shipsByTypeBefore.entrySet() ) {
            int shipsDiff = shipsByTypeBefore.get( e.getKey() );
            try {
                shipsDiff -= shipsByTypeAfter.get( e.getKey() );
            } catch ( NullPointerException npe ) {
                // no ships after battle
            }
            gain = gain.add( e.getKey().price.mul( shipsDiff ) );

        }

        return gain;
    }

    private static Map<Ship.Type, Integer> getShipsByType( List<Ship> fleet ) {
        Map<Ship.Type, Integer> shipsByType = Maps.newHashMap();
        for ( Ship s : fleet ) {
            Integer cnt = shipsByType.get( s.getType() );
            shipsByType.put( s.getType(), ( cnt == null ? 1 : cnt + 1 ) );
        }

        return shipsByType;
    }

    private static void simulate( User attacker, User defender ) {
        for ( int round = 0; round < MAX_ROUNDS; round++ ) {
            attacker.prepareForNextRound();
            defender.prepareForNextRound();

            for ( Ship attackerShip : attacker.getFleet() ) {
                attackerShip.attack( defender.getFleet() );
            }
            for ( Ship defenderShip : defender.getFleet() ) {
                defenderShip.attack( attacker.getFleet() );
            }

            attacker.calculateLosses();
            defender.calculateLosses();

            if ( attacker.isDefeated() || defender.isDefeated() ) {
                break;
            }
        }
    }
}

