package ogame;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

class Ship {
    private static final double CAN_EXPLODE = 0.7;
    private Random rnd = new Random();
    private boolean isExplode = false;
    private double structure;
    private double shield;
    private Map<Type, Integer> rapidfire = new HashMap<Type, Integer>();
    private Type type;

    Ship( Type type ) {
        this.type = type;
        this.structure = type.structure;

        this.prepareForNextRound();
    }

    public boolean attack( Ship dfShip ) {
        dfShip.receiveDamage( this.type.attack );

        Integer rapidCnt = this.rapidfire.get( dfShip.type );
        boolean hasRapid = rapidCnt != null && rapidCnt > 1;
        if ( rapidCnt != null ) {
            this.rapidfire.put( dfShip.type, rapidCnt - 1 );
        }
        return hasRapid;
    }

    private void receiveDamage( double damage ) {
        double damageToStructure = 0;
        if ( damage >= shield ) {
            damageToStructure = damage - shield;
            shield = 0;
        } else {
            shield -= Math.floor( damage / ( shield / 100 ) ) * ( shield / 100 );
        }

        structure -= damageToStructure;

        if ( structure <= 0 || this.structure / this.type.structure <= CAN_EXPLODE ) {
            isExplode = isExplode || rnd.nextInt( 100 ) < ( 100 - ( structure / type.structure ) * 100 );
        }
    }

    public boolean isSurvive() {
        boolean isSurvive = this.structure > 0 && !isExplode;
        return isSurvive;
    }

    public void prepareForNextRound() {
        this.rapidfire.putAll( type.rapidfire );
        ;
        this.shield = type.shield;
    }

    public Type getType() {
        return type;
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
        SS( 200, 1, 1, new HashMap<Type, Integer>() ), // solar satelite
        LINKOR( 6000, 200, 1000, new HashMap<Type, Integer>() {{ // linkor
            put( EP, 5 );
            put( SS, 5 );
        }} ),


        RL( 200, 20, 80, new HashMap<Type, Integer>() ), // rocket louncher
        ;
        private double structure;
        private double shield;
        private double attack;
        private Map<Type, Integer> rapidfire = new HashMap<Type, Integer>();

        private Type( double structure, double shield, double attack, Map<Type, Integer> rapidfire ) {
            this.structure = structure;
            this.shield = shield;
            this.attack = attack;
            this.rapidfire.putAll( rapidfire );
        }

    }
}
