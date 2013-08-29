package ua.net.tokar.ogame;

import com.google.common.collect.Maps;

import java.util.*;

public class Main {
    private final static int MAX_ROUNDS = 6;
    final static double STRUCTURE_INTO_DEBRIS_FACTOR = 0.3;
    final static int RECYCLER_CAPACITY = 20000;

    final public static int SIM_CNT = 20;
    final public static int MAX_GENERATIONS = 1000;

    final public static int POPULATION_SIZE = 6;

    public static void main( String[] args ) {
        long start = System.currentTimeMillis();
//        simple();
        genetic();

        System.out.println( String.format( "Elapsed: %dms", System.currentTimeMillis() - start ) );
    }


    private static final int GEN_LENGTH = 9;

    private static void genetic() {
        Ship.Type[] fleetTypes = new Ship.Type[]{
                Ship.Type.SC,
                Ship.Type.LC,
                Ship.Type.LF,
                Ship.Type.HF,
                Ship.Type.CRUISER,
                Ship.Type.BATTLESHIP,
                Ship.Type.RECYCLER,
                Ship.Type.EP,
                Ship.Type.BATTLECRUISER,
        };
//        int[] sampleFleet = { 80, 80, 1000, 22, 220, 190, 240, 21, 61 };
        int[] sampleFleet = { 0, 0, 10000, 0, 0, 0, 0, 0, 0 };

        int[][] population = new int[ POPULATION_SIZE ][ GEN_LENGTH ];

        // do init initial random population
        for ( int i = 0; i < POPULATION_SIZE - 1; i++ ) {
            for ( int j = 0; j < sampleFleet.length; j++ ) {
                population[ i ][ j ] = (int) ( Math.random() * sampleFleet[ j ] );
            }
        }
        population[ population.length - 1 ] = sampleFleet;

        Map<Ship.Type, Integer> defenderShipsByTypeBeforeBattle = Maps.newHashMap();
        defenderShipsByTypeBeforeBattle.put( Ship.Type.BATTLECRUISER, 100 );
//        defenderShipsByTypeBeforeBattle.put( Ship.Type.CRUISER, 10 );
//        defenderShipsByTypeBeforeBattle.put( Ship.Type.HF, 20 );
//        defenderShipsByTypeBeforeBattle.put( Ship.Type.LF, 50 );
//        defenderShipsByTypeBeforeBattle.put( Ship.Type.LC, 8 );
//        defenderShipsByTypeBeforeBattle.put( Ship.Type.SC, 13 );
//        defenderShipsByTypeBeforeBattle.put( Ship.Type.RECYCLER, 4 );
//        defenderShipsByTypeBeforeBattle.put( Ship.Type.EP, 22 );
//        defenderShipsByTypeBeforeBattle.put( Ship.Type.SS, 4 );

        User defender = new User( new Researches( 0, 0, 0 ) );

        User attacker = new User( new Researches( 0, 0, 0 ) );

        for ( int i = 0; i < MAX_GENERATIONS; i++ ) {
            List<Result> results = new ArrayList<Result>();

            for ( int popN = 0; popN < population.length; popN++ ) {
                List<CombatReport> reports = new ArrayList<CombatReport>();

                Map<Ship.Type, Integer> attackerShipsByTypeBeforeBattle = Maps.newHashMap();
                for ( int j = 0; j < GEN_LENGTH; j++ ) {
                    attackerShipsByTypeBeforeBattle.put( fleetTypes[ j ], population[ popN ][ j ] );
                }

                for ( int simNum = 0; simNum < SIM_CNT; simNum++ ) {
                    defender.setFleet( defenderShipsByTypeBeforeBattle );
                    attacker.setFleet( attackerShipsByTypeBeforeBattle );


                    simulate( attacker, defender );

                    CombatReport report = new CombatReport(
                            attackerShipsByTypeBeforeBattle,
                            attacker.getShipsByType(),
                            defenderShipsByTypeBeforeBattle,
                            defender.getShipsByType()
                    );

                    reports.add( report );
                }

                int gainSum = 0;
                for ( CombatReport report : reports ) {
                    gainSum += report.gainAttacker.toMetal( 3, 2, 1 ) / 1000;
                }

                results.add( new Result( population[ popN ], gainSum / SIM_CNT, attacker.getFleetCost().toMetal( 3, 2, 1 ) / 1000 ) );
            }

            Collections.sort( results, new ResultCmp() );
            for ( int j = 0; j < population.length; j++ ) {
//                System.out.println( results.get( j ).gain + " " + results.get( j ).fleetCost + " " + Arrays.toString( results.get( j ).spec  ) );
                population[ j ] = results.get( j ).spec;
            }
//            System.out.println( "--" );


            int[][] crossed = cross( Arrays.copyOfRange( population, 0, population.length / 2 ) );
            int[][] mutated = normalize( mutate( crossed, 1 ), sampleFleet );
////            population = normalize( crossed, sampleFleet );
//
//            System.out.println( "Initial pop:" );
//            for ( int[] arr : population ) {
//                System.out.println( Arrays.toString(  arr  ) );
//            }
//            System.out.println( "\nCrossed pop:" );
//            for ( int[] arr : crossed ) {
//                System.out.println( Arrays.toString(  arr  ) );
//            }
//            System.out.println( "\nMutated pop:" );
//            for ( int[] arr : mutated ) {
//                System.out.println( Arrays.toString(  arr  ) );
//            }

            population = mutated;
            if ( i % 10 == 0 ) {
                System.out.print( "." );
            }
            if ( i % 100 == 0 ) {
                System.out.print( i * 100 / MAX_GENERATIONS + "%" );
            }

        }
        System.out.println( "\n----------------------------------" );
        System.out.println( Arrays.toString( population[ 0 ] ) );
    }

    public static class Result {
        public final int[] spec;
        public final int fleetCost;
        public final int gain;

        public Result( int[] spec, int gain, int fleetCost ) {
            this.spec = spec;
            this.gain = gain;
            this.fleetCost = fleetCost;
        }
    }

    public static class ResultCmp implements Comparator<Result> {
        @Override
        public int compare( Result r1, Result r2 ) {
            int res = ( ( r2.fleetCost == 0 )
                    ? 0
                    : ( r2.gain * 10000 / r2.fleetCost ) ) -
                    ( ( r1.fleetCost == 0 )
                            ? 0
                            : ( r1.gain * 10000 / r1.fleetCost ) );

            return res;
        }
    }

    private static int[][] normalize( int[][] population, int[] sample ) {
        for ( int i = 0; i < population.length; i++ ) {
            for ( int j = 0; j < sample.length; j++ ) {
                population[ i ][ j ] = Math.max( Math.min( population[ i ][ j ], sample[ j ] ), 0 );
            }
        }

        return population;
    }

    private static int[][] mutate( int[][] population, double percent ) {
        Random rnd = new Random();

        for ( int i = 0; i < population.length / 2 * percent; i++ ) {
            int idx = rnd.nextInt( population.length / 2 ) + population.length / 2;
            int jdx = rnd.nextInt( GEN_LENGTH );
            population[ idx ][ jdx ] *= 2 * rnd.nextDouble();
        }

        return population;
    }

    private static int[][] cross( int[][] population ) {
        Random rnd = new Random();

        int[][] newPop = new int[ population.length * 2 ][ GEN_LENGTH ];
        System.arraycopy( population, 0, newPop, 0, population.length );

        for ( int i = 0; i < population.length; i++ ) {
            System.arraycopy( population[ i ], 0, newPop[ i + population.length ], 0, GEN_LENGTH / 2 );
            System.arraycopy(
                    population[ rnd.nextInt( population.length ) ],
                    GEN_LENGTH / 2,
                    newPop[ i + population.length ],
                    GEN_LENGTH / 2,
                    GEN_LENGTH / 2
            );
        }

        return newPop;
    }

    public static String toString( int[][] M ) {
        String separator = ", ";
        StringBuffer result = new StringBuffer();

        // iterate over the first dimension
        for ( int i = 0; i < M.length; i++ ) {
            // iterate over the second dimension
            for ( int j = 0; j < M[ i ].length; j++ ) {
                result.append( M[ i ][ j ] );
                result.append( separator );
            }
            // remove the last separator
            result.setLength( result.length() - separator.length() );
            // add a line break.
            result.append( "\n" );
        }
        return result.toString();
    }

    public static void simple() {
        Map<Ship.Type, Integer> attackerShipsByTypeBeforeBattle = Maps.newHashMap();
        attackerShipsByTypeBeforeBattle.put( Ship.Type.CRUISER, 10 );

        Map<Ship.Type, Integer> defenderShipsByTypeBeforeBattle = Maps.newHashMap();
        defenderShipsByTypeBeforeBattle.put( Ship.Type.LF, 100 );

        // -------  SIMULATE  --------

        User attacker = new User( new Researches( 8, 7, 8 ) );
        attacker.setFleet( attackerShipsByTypeBeforeBattle );

        User defender = new User( new Researches( 8, 7, 8 ) );
        defender.setFleet( defenderShipsByTypeBeforeBattle );

        Price attackerFleetCost = attacker.getFleetCost();
        Price defenderFleetCost = defender.getFleetCost();

        simulate( attacker, defender );


        // --------  REPORT  -----------

        CombatReport report = new CombatReport(
                attackerShipsByTypeBeforeBattle,
                attacker.getShipsByType(),
                defenderShipsByTypeBeforeBattle,
                defender.getShipsByType()
        );


//        System.out.println( "Attacker: " + attacker + "/" + attackerFleetCost );
//        System.out.println( "Defender: " + defender + "/" + defenderFleetCost );

//        System.out.println( "Debris: " + report.debrisField );
//        System.out.println( "Recyclers cnt: " + report.neededRecyclers );

//        System.out.println( "Attacker loss: " + report.lossAttacker );
//        System.out.println( "Defender loss: " + report.lossDefender );

//        System.out.println( "Attacker gain: " + report.gainAttacker );
//        System.out.println( "Defender gain: " + report.gainDefender );
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

