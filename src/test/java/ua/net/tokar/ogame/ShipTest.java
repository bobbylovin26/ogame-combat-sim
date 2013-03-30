package ua.net.tokar.ogame;


import junit.framework.Assert;
import org.junit.Test;

public class ShipTest {
    private final static Researches NONE_RESEARCHES = new Researches( 0, 0, 0 );

    @Test
    public void basicShipCharacteristics() {
        Ship cruiser = new Ship( Ship.Type.CRUISER, NONE_RESEARCHES );

        Assert.assertEquals( Ship.Type.CRUISER.basicStructure, cruiser.getDefence() );
        Assert.assertEquals( Ship.Type.CRUISER.basicShields, cruiser.getShields() );
        Assert.assertEquals( Ship.Type.CRUISER.basicAttack, cruiser.getAttack() );
    }

    @Test
    public void shipCharacteristicsWithResearches() {
        Ship cruiser = new Ship( Ship.Type.CRUISER, new Researches( 8, 7, 8 ) );

        Assert.assertEquals( 4860.0, cruiser.getDefence() );
        Assert.assertEquals( 85.0, cruiser.getShields() );
        Assert.assertEquals( 720.0, cruiser.getAttack() );
    }

    @Test
    public void shiedConsumption() {
        Ship cruiser = new Ship( Ship.Type.CRUISER, NONE_RESEARCHES );

        cruiser.receiveDamage( 0.1 );
        Assert.assertEquals( Ship.Type.CRUISER.basicShields, cruiser.getShields() );

        cruiser.receiveDamage( 0.4 );
        Assert.assertEquals( Ship.Type.CRUISER.basicShields, cruiser.getShields() );

        cruiser.receiveDamage( 1 );
        Assert.assertEquals( 49.0, cruiser.getShields() );
    }

    @Test
    public void shieldsCunsumptsBeforeStructure() {
        Ship cruiser = new Ship( Ship.Type.CRUISER, NONE_RESEARCHES );

        cruiser.receiveDamage( 50 );
        Assert.assertEquals( Ship.Type.CRUISER.basicStructure, cruiser.getDefence() );

        cruiser.receiveDamage( 50 );
        Assert.assertEquals( 2650.0, cruiser.getDefence() );
    }

    @Test
    public void powerfullDamageConsumsShieldsAndStructure() {
        Ship cruiser = new Ship( Ship.Type.CRUISER, NONE_RESEARCHES );

        cruiser.receiveDamage( 750 );
        Assert.assertEquals( 2000.0, cruiser.getDefence() );
    }

    @Test
    public void shieldsRestoresOnNewRoundButNotStructure() {
        Ship cruiser = new Ship( Ship.Type.CRUISER, NONE_RESEARCHES );

        cruiser.receiveDamage( 750 );
        Assert.assertEquals( 2000.0, cruiser.getDefence() );
        Assert.assertEquals( 0.0, cruiser.getShields() );

        cruiser.prepareForNextRound();

        Assert.assertEquals( 2000.0, cruiser.getDefence() );
        Assert.assertEquals( Ship.Type.CRUISER.basicShields, cruiser.getShields() );
    }

    @Test
    public void shipHasRapidFireVersusSomeTypes() {
        Ship cruiser = new Ship( Ship.Type.CRUISER, NONE_RESEARCHES );

        Ship shipForRapidfire = new Ship( Ship.Type.LF, NONE_RESEARCHES );
        Ship shipWithoutRapidfire = new Ship( Ship.Type.HF, NONE_RESEARCHES );

        Assert.assertTrue( cruiser.doFire( shipForRapidfire ) );
        Assert.assertFalse( cruiser.doFire( shipWithoutRapidfire ) );
    }
}
