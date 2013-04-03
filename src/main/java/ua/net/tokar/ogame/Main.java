package ua.net.tokar.ogame;

import com.google.common.collect.Maps;

import java.util.*;

public class Main {
    private final static int MAX_ROUNDS = 6;
    final static double STRUCTURE_INTO_DEBRIS_FACTOR = 0.3;
    final static int RECYCLER_CAPACITY = 20000;

    final public static int SIM_CNT = 100;

    public static void main( String[] args ) {
//        simple();
        genetic();
    }


    private static final int GEN_LENGTH = 6;
    private static void genetic() {
        Ship.Type[] fleetTypes = new Ship.Type[] {
                Ship.Type.SC,
                Ship.Type.LC,
                Ship.Type.LF,
                Ship.Type.HF,
                Ship.Type.CRUISER,
                Ship.Type.BATTLESHIP
        };
        int[] sampleFleet = {0,0,39,5,19,55};

        int[][] pop = new int[][] {
                {0,1,2,3,4,5},
                {5,4,3,2,1,0},
                {0,0,0,0,0,0},
                {1,1,1,1,1,1},
                {9,9,9,9,9,9},
                sampleFleet,
        };

        Map<Ship.Type, Integer> defenderShipsByTypeBeforeBattle = Maps.newHashMap();
        defenderShipsByTypeBeforeBattle.put( Ship.Type.BATTLESHIP, 14 );
        defenderShipsByTypeBeforeBattle.put( Ship.Type.CRUISER, 8 );
        defenderShipsByTypeBeforeBattle.put( Ship.Type.HF, 4 );
        defenderShipsByTypeBeforeBattle.put( Ship.Type.LC, 2 );
        defenderShipsByTypeBeforeBattle.put( Ship.Type.SC, 56 );

        User defender = new User( new Researches( 8, 7, 8 ) );

        User attacker = new User( new Researches( 7, 7, 7 ) );

        for ( int i = 0; i < 10000; i++ ) {
            List<Result> results = new LinkedList<Result>();

            for ( int popN = 0; popN < pop.length; popN++ ) {
                List<CombatReport> reports = new LinkedList<CombatReport>();

                Map<Ship.Type, Integer> attackerShipsByTypeBeforeBattle = Maps.newHashMap();
                for ( int j = 0; j < GEN_LENGTH; j++ ) {
                    attackerShipsByTypeBeforeBattle.put( fleetTypes[ j ], pop[ popN ][ j ] );
                }
                Price attackerFleetCost = attacker.getFleetCost();

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
                    gainSum += report.gainAttacker.toMetal( 3,2,1 )/1000;
                }

                results.add( new Result( pop[popN], gainSum / SIM_CNT, attackerFleetCost.toMetal( 3,2,1 )/1000 ) );
            }

            Collections.sort( results, new ResultCmp() );
            for ( int j = 0; j < pop.length; j++ ) {
                pop[j] = results.get( j ).spec;
            }

            int[][] crossed = cross( Arrays.copyOfRange( pop, 0, pop.length / 2 ) );
            int[][] mutated = mutate( crossed, 0.1 );
            pop = normalize( mutated, sampleFleet );

            if ( i % 100 == 0 ) {
                System.out.println( "----------------------------------" );
                System.out.println( "Generation: " + i + " bestGain " + results.get( 0 ).gain + ", fleetCost: " + results.get( 0 ).fleetCost );
                System.out.println( Arrays.toString( pop[0] ) );
            }
        }
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
            int res = r2.gain - r1.gain;

            if ( res == 0 ) {
                res = r1.fleetCost - r2.fleetCost;
            }

            return res;
        }
    }

    private static int[][] normalize( int[][] population, int[] sample ) {
        for ( int i = 0; i < population.length; i++ ) {
            for ( int j = 0; j < sample.length; j++ ) {
                population[i][j] = Math.max( Math.min( population[i][j], sample[j] ), 0 );
            }
        }

        return population;
    }

    private static int[][] mutate( int[][] population, double percent ) {
        Random rnd = new Random();

        for ( int i = 0; i < population.length / 2 * percent; i++ ) {
            population[rnd.nextInt( population.length / 2 ) + population.length / 2][ rnd.nextInt( GEN_LENGTH )] += 10 - rnd.nextInt( 20 );
        }

        return population;
    }

    private static int[][] cross( int[][] population ) {
        Random rnd = new Random();

        int[][] newPop = new int[population.length * 2][GEN_LENGTH];
        System.arraycopy( population, 0, newPop, 0, population.length );

        for ( int i = 0; i < population.length; i++ ) {
            System.arraycopy( population[ i ], 0, newPop[i + population.length], 0, GEN_LENGTH / 2 );
            System.arraycopy(
                    population[ rnd.nextInt( population.length ) ],
                    GEN_LENGTH / 2,
                    newPop[i + population.length],
                    GEN_LENGTH / 2,
                    GEN_LENGTH / 2
            );
        }

        return newPop;
    }

    public static String toString(int[][] M) {
        String separator = ", ";
        StringBuffer result = new StringBuffer();

        // iterate over the first dimension
        for (int i = 0; i < M.length; i++) {
            // iterate over the second dimension
            for(int j = 0; j < M[i].length; j++){
                result.append(M[i][j]);
                result.append(separator);
            }
            // remove the last separator
            result.setLength(result.length() - separator.length());
            // add a line break.
            result.append("\n");
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


        System.out.println( "Attacker: " + attacker + "/" + attackerFleetCost );
//        System.out.println( "Defender: " + defender + "/" + defenderFleetCost );

//        System.out.println( "Debris: " + report.debrisField );
//        System.out.println( "Recyclers cnt: " + report.neededRecyclers );

//        System.out.println( "Attacker loss: " + report.lossAttacker );
//        System.out.println( "Defender loss: " + report.lossDefender );

        System.out.println( "Attacker gain: " + report.gainAttacker );
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

