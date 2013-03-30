package ogame;

public class Main {
    public static void main( String[] args ) {
        User attacker = new User( new Researches( 8, 7, 8 ) );
        attacker.addFleet( Ship.Type.LF, 100 );

        User defender = new User( new Researches( 8, 7, 8 ) );
        defender.addFleet( Ship.Type.CRUISER, 10 );

        simulate( attacker, defender );
    }

    private static int MAX_ROUNDS = 6;

    private static void simulate( User attacker, User defender ) {
        for ( int round = 0; round < MAX_ROUNDS; round++ ) {
            System.out.println( String.format( "Round %d starts", round + 1 ) );
            attacker.prepareForNextRound();
            defender.prepareForNextRound();

            for ( Ship attackerShip : attacker.getFleet() ) {
                boolean hasRapidfire;
                do {
                    hasRapidfire = attackerShip.attack( defender.getTarget() );
                } while ( hasRapidfire );
            }
            for ( Ship defenderShip : defender.getFleet() ) {
                boolean hasRapidfire;
                do {
                    hasRapidfire = defenderShip.attack( attacker.getTarget() );
                } while ( hasRapidfire );
            }

            attacker.calculateLosses();
            defender.calculateLosses();

            System.out.println( "Attacker: " + attacker );
            System.out.println( "Defender: " + defender );
            if ( attacker.isDefeated() || defender.isDefeated() ) {
                break;
            }
        }
    }
}

