package ua.net.tokar.ogame;

public class Price {
    public final int metal;
    public final int crystal;
    public final int deuterium;

    public Price( int metal, int crystal, int deuterium ) {
        this.deuterium = deuterium;
        this.crystal = crystal;
        this.metal = metal;
    }

    public Price add( Price price ) {
        int metal = this.metal + price.metal;
        int crystal = this.crystal + price.crystal;
        int deuterium = this.deuterium + price.deuterium;

        return new Price( metal, crystal, deuterium );
    }

    public Price mul( double factor ) {
        int metal = (int) ( this.metal * factor );
        int crystal = (int) ( this.crystal * factor );
        int deuterium = (int) ( this.deuterium * factor );

        return new Price( metal, crystal, deuterium );
    }

    public Price withoutDeuterium() {
        return new Price( metal, crystal, 0 );
    }

    @Override
    public String toString() {
        return "Price{" +
                "metal=" + metal +
                ", crystal=" + crystal +
                ", deuterium=" + deuterium +
                '}';
    }
}
