package ua.net.tokar.ogame;

public class Main {
    public static void main( String[] args ) {
        User attacker = new User( new Researches( 8, 7, 8 ) );
        attacker.addFleet( Ship.Type.LF, 100 );

        User defender = new User( new Researches( 8, 7, 8 ) );
        defender.addFleet( Ship.Type.CRUISER, 5 );

        simulate( attacker, defender );

        System.out.println( "Attacker: " + attacker );
        System.out.println( "Defender: " + defender );
    }

    private static int MAX_ROUNDS = 6;

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

