package ua.net.tokar.ogame;

import java.util.Map;

public class CombatReport {
    public final Price lossAttacker;
    public final Price lossDefender;
    public final Price debrisField;
    public final Price gainAttacker;
    public final Price gainDefender;
    public final int neededRecyclers;

    public CombatReport(
            Map<Ship.Type, Integer> attackerShipsByTypeBeforeBattle,
            Map<Ship.Type, Integer> attackerShipsByTypeAfterBattle,
            Map<Ship.Type, Integer> defenderShipsByTypeBeforeBattle,
            Map<Ship.Type, Integer> defenderShipsByTypeAfterBattle
    ) {
        lossAttacker = calculateLoss( attackerShipsByTypeBeforeBattle, attackerShipsByTypeAfterBattle );
        lossDefender = calculateLoss( defenderShipsByTypeBeforeBattle, defenderShipsByTypeAfterBattle );

        debrisField = lossAttacker.add( lossDefender ).withoutDeuterium().mul( Main.STRUCTURE_INTO_DEBRIS_FACTOR );

        gainAttacker = debrisField.add( lossAttacker.mul( -1 ) );
        gainDefender = debrisField.add( lossDefender.mul( -1 ) );

        neededRecyclers = (int) Math.ceil( (double) ( debrisField.metal + debrisField.crystal ) / Main.RECYCLER_CAPACITY );
    }

    private Price calculateLoss( Map<Ship.Type, Integer> shipsByTypeBefore, Map<Ship.Type, Integer> shipsByTypeAfter ) {
        Price loss = new Price( 0, 0, 0 );
        for ( Map.Entry<Ship.Type, Integer> e : shipsByTypeBefore.entrySet() ) {
            int shipsDiff = shipsByTypeBefore.get( e.getKey() );
            try {
                shipsDiff -= shipsByTypeAfter.get( e.getKey() );
            } catch ( NullPointerException npe ) {
                // no ships after battle
            }
            loss = loss.add( e.getKey().price.mul( shipsDiff ) );

        }

        return loss;
    }
}
