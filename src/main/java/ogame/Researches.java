package ogame;

class Researches {
    private int defence = 0;
    private int shields = 0;
    private int attack = 0;

    Researches( int defence, int shields, int attack ) {
        this.defence = defence;
        this.shields = shields;
        this.attack = attack;
    }

    public int getDefence() {
        return defence;
    }

    public int getShields() {
        return shields;
    }

    public int getAttack() {
        return attack;
    }
}
