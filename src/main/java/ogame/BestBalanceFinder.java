package ogame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BestBalanceFinder {
    public static void main( String[] args ) {
        Researches attackerResearches = new Researches( 8, 7, 8 );
        Researches defenderResearches = new Researches( 8, 7, 8 );

        List<Ship> attackerFleet = new ArrayList<Ship>();
        attackerFleet.addAll( build( 100, Ship.Type.LF ) );

        List<Ship> defenderFleet = new ArrayList<Ship>();
        defenderFleet.addAll( build( 10, Ship.Type.CRUISER ) );

        simulate( attackerResearches, attackerFleet, defenderResearches, defenderFleet );
    }

    public static List<Ship> build( int cnt, Ship.Type type ) {
        List<Ship> ships = new ArrayList<Ship>( cnt );
        for ( int i = 0; i < cnt; i++ ) {
            ships.add( new Ship( type ) );
        }

        return ships;
    }


    private static int MAX_ROUNDS = 6;

    private static void simulate( Researches attackerResearches, List<Ship> attackerFleet, Researches defenderResearches, List<Ship> defenderFleet ) {
        Random rnd = new Random();
        for ( int i = 0; i < MAX_ROUNDS; i++ ) {
            System.out.println( String.format( "Round %d starts", i + 1 ) );

            for ( Ship attackerShip : attackerFleet ) {
                boolean hasRapidfire = false;
                do {
                    int idx = rnd.nextInt( defenderFleet.size() );
                    Ship dfShip = defenderFleet.get( idx );

                    hasRapidfire = attackerShip.attack( dfShip );
                } while ( hasRapidfire );
            }
            for ( Ship dfShip : defenderFleet ) {
                boolean hasRapidfire = false;
                do {
                    int idx = rnd.nextInt( attackerFleet.size() );
                    Ship attackerShip = attackerFleet.get( idx );

                    hasRapidfire = dfShip.attack( attackerShip );
                } while ( hasRapidfire );
            }


            for ( int shipIdx = attackerFleet.size() - 1; shipIdx >= 0; shipIdx-- ) {
                if ( !attackerFleet.get( shipIdx ).isSurvive() ) {
                    attackerFleet.remove( shipIdx );
                } else {
                    attackerFleet.get( shipIdx ).prepareForNextRound();
                }
            }
            for ( int shipIdx = defenderFleet.size() - 1; shipIdx >= 0; shipIdx-- ) {
                if ( !defenderFleet.get( shipIdx ).isSurvive() ) {
                    defenderFleet.remove( shipIdx );
                } else {
                    defenderFleet.get( shipIdx ).prepareForNextRound();
                }
            }

            System.out.println( "Attacker: " + attackerFleet.size() );
            System.out.println( "Defender: " + defenderFleet.size() );
            if ( attackerFleet.isEmpty() || defenderFleet.isEmpty() ) {
                break;
            }
        }
    }
}

