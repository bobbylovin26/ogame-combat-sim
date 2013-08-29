package ua.net.tokar.ogame;

import java.util.HashMap;
import java.util.List;
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
    private final double basicShields;
    private final double attack;
    private final double basicStructure;

    public Ship( Type type, Researches researches ) {
        this.type = type;
        this.researches = researches;
        this.basicShields = type.basicShields + type.basicShields * 0.1 * researches.getShields();
        this.attack = this.type.basicAttack + this.type.basicAttack * 0.1 * researches.getAttack();

        this.basicStructure = type.basicStructure + type.basicStructure * 0.1 * researches.getDefence();
        this.structure = basicStructure;

        prepareForNextRound();
    }

    public double getAttack() {
        return attack;
    }

    public void attack( List<Ship> fleet ) {
        if ( fleet.isEmpty() ) {
            return;
        }

        boolean hasRapidfire;
        do {
            int idx = rnd.nextInt( fleet.size() );
            hasRapidfire = doFire( fleet.get( idx ) );
        } while ( hasRapidfire );
    }

    public boolean doFire( Ship target ) {
        target.receiveDamage( attack );

        Integer rapidCnt = this.rapidfire.get( target.type );
        if ( rapidCnt != null && rapidCnt > 0 ) {
            this.rapidfire.put( target.type, rapidCnt - 1 );
        }

        boolean canFireAgain = rapidCnt != null && rapidCnt > 1;

        return canFireAgain;
    }

    public void receiveDamage( double damage ) {
        if ( !isSurvive() ) {
            return;
        }

        double damageToStructure = 0;
        if ( damage >= shields ) {
            damageToStructure = damage - shields;
            shields = 0;
        } else {
            double shieldMinConsumptionPart = shields / 100;
            shields -= Math.floor( damage / shieldMinConsumptionPart ) * shieldMinConsumptionPart;
        }

        structure -= damageToStructure;

        double dmgPart = getDamagePart();
        if ( structure <= 0 || dmgPart <= CAN_EXPLODE ) {
            isExplode = isExplode || rnd.nextDouble() < ( 1 - dmgPart );
        }
    }

    private double getDamagePart() {
        return structure / basicStructure;
    }

    public boolean isSurvive() {
        boolean doNotSurvive = isExplode || this.structure <= 0;
        return !doNotSurvive;
    }

    public void prepareForNextRound() {
        this.rapidfire.putAll( type.rapidfire );

        this.shields = basicShields;
    }

    public double getDefence() {
        return structure;
    }

    public double getShields() {
        return shields;
    }

    public Type getType() {
        return type;
    }

    public static enum Type {
        LF( new Price( 3000, 1000, 0 ), 10, 50, new HashMap<Type, Integer>() {{ // light fighter
            put( EP, 5 );
            put( SS, 5 );
        }} ),
        HF( new Price( 6000, 4000, 0 ), 25, 150, new HashMap<Type, Integer>() {{ // heavy fighter
            put( EP, 5 );
            put( SS, 5 );
            put( SC, 3 );
        }} ),
        CRUISER( new Price( 20000, 7000, 2000 ), 50, 400, new HashMap<Type, Integer>() {{  // cruiser
            put( EP, 5 );
            put( SS, 5 );
            put( LF, 6 );
            put( RL, 10 );
        }} ),
        BATTLESHIP( new Price( 45000, 150000, 0 ), 200, 1000, new HashMap<Type, Integer>() {{ // linkor
            put( EP, 5 );
            put( SS, 5 );
        }} ),
        BATTLECRUISER( new Price( 30000, 40000, 15000 ), 400, 700, new HashMap<Type, Integer>() {{  // cruiser
            put( EP, 5 );
            put( SS, 5 );
            put( SC, 3 );
            put( LC, 3 );
            put( HF, 4 );
            put( CRUISER, 4 );
            put( BATTLESHIP, 7 );
        }} ),
        SC( new Price( 2000, 2000, 0 ), 10, 5, new HashMap<Type, Integer>() {{ // small cargo
            put( EP, 5 );
            put( SS, 5 );
        }} ),
        LC( new Price( 6000, 6000, 0 ), 25, 5, new HashMap<Type, Integer>() {{ // large cargo
            put( EP, 5 );
            put( SS, 5 );
        }} ),
        RECYCLER( new Price( 10000, 6000, 2000 ), 10, 1, new HashMap<Type, Integer>() {{ // large cargo
            put( EP, 5 );
            put( SS, 5 );
        }} ),
        EP( new Price( 0, 1000, 0 ), 0.01, 0.01, new HashMap<Type, Integer>() ),// espionage probe
        SS( new Price( 0, 2000, 500 ), 1, 1, new HashMap<Type, Integer>() ), // solar satellite


        RL( new Price( 2000, 0, 0 ), 20, 80, new HashMap<Type, Integer>() ), // rocket launcher
        ;


        public final double basicStructure;
        public final double basicShields;
        public final double basicAttack;
        public final Price price;
        private final Map<Type, Integer> rapidfire = new HashMap<Type, Integer>();

        private Type( Price price, double basicShields, double basicAttack, Map<Type, Integer> rapidfire ) {
            this.price = price;
            this.basicStructure = (double) ( price.crystal + price.metal ) / 10.0;
            this.basicShields = basicShields;
            this.basicAttack = basicAttack;

            this.rapidfire.putAll( rapidfire );
        }

    }
}
