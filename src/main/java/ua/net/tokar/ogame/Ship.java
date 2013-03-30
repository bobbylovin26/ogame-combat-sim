package ua.net.tokar.ogame;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

class Ship {
    private static final double CAN_EXPLODE = 0.7;
    private Random rnd = new Random();
    private boolean isExplode = false;
    private double structure;
    private double shields;
    private final Map<Type, Integer> rapidfire = new HashMap<Type, Integer>();
    private final Type type;
    private final Researches researches;

    public Ship( Type type, Researches researches ) {
        this.type = type;
        this.researches = researches;

        this.structure = type.basicStructure + type.basicStructure * 0.1 * researches.getDefence();

        prepareForNextRound();
    }

    public double getAttack() {
        return this.type.basicAttack + this.type.basicAttack * 0.1 * researches.getAttack();
    }

    public boolean attack( Ship target ) {
        target.receiveDamage( getAttack() );

        Integer rapidCnt = this.rapidfire.get( target.type );
        boolean hasRapid = rapidCnt != null && rapidCnt > 1;
        if ( rapidCnt != null ) {
            this.rapidfire.put( target.type, rapidCnt - 1 );
        }
        return hasRapid;
    }

    public void receiveDamage( double damage ) {
        double damageToStructure = 0;
        if ( damage >= shields ) {
            damageToStructure = damage - shields;
            shields = 0;
        } else {
            shields -= Math.floor( damage / ( shields / 100 ) ) * ( shields / 100 );
        }

        structure -= damageToStructure;

        if ( structure <= 0 || this.structure / this.type.basicStructure <= CAN_EXPLODE ) {
            isExplode = isExplode || rnd.nextInt( 100 ) < ( 100 - ( structure / type.basicStructure ) * 100 );
        }
    }

    public boolean isSurvive() {
        boolean isSurvive = this.structure > 0 && !isExplode;
        return isSurvive;
    }

    public void prepareForNextRound() {
        this.rapidfire.putAll( type.rapidfire );

        this.shields = type.basicShields + type.basicShields * 0.1 * researches.getShields();
    }

    public double getDefence() {
        return structure;
    }

    public double getShields() {
        return shields;
    }

    public static enum Type {
        LF( 400, 10, 50, new HashMap<Type, Integer>() {{ // light fighter
            put( EP, 5 );
            put( SS, 5 );
        }} ),
        HF( 1000, 25, 150, new HashMap<Type, Integer>() {{ // heavy fighter
            put( EP, 5 );
            put( SS, 5 );
            put( SC, 3 );
        }} ),
        CRUISER( 2700, 50, 400, new HashMap<Type, Integer>() {{  // cruiser
            put( EP, 5 );
            put( SS, 5 );
            put( LF, 6 );
            put( RL, 10 );
        }} ),
        SC( 400, 10, 5, new HashMap<Type, Integer>() {{ // small cargo
            put( EP, 5 );
            put( SS, 5 );
        }} ),
        LC( 1200, 25, 5, new HashMap<Type, Integer>() {{ // large cargo
            put( EP, 5 );
            put( SS, 5 );
        }} ),
        EP( 100, 0.01, 0.01, new HashMap<Type, Integer>() ),// espionage probe
        SS( 200, 1, 1, new HashMap<Type, Integer>() ), // solar satellite
        LINKOR( 6000, 200, 1000, new HashMap<Type, Integer>() {{ // linkor
            put( EP, 5 );
            put( SS, 5 );
        }} ),


        RL( 200, 20, 80, new HashMap<Type, Integer>() ), // rocket launcher
        ;
        private double basicStructure;
        private double basicShields;
        private double basicAttack;
        private Map<Type, Integer> rapidfire = new HashMap<Type, Integer>();

        private Type( double basicStructure, double basicShields, double basicAttack, Map<Type, Integer> rapidfire ) {
            this.basicStructure = basicStructure;
            this.basicShields = basicShields;
            this.basicAttack = basicAttack;

            this.rapidfire.putAll( rapidfire );
        }

    }
}
