package ua.net.tokar.ogame;

public class Price {
    public final int metal;
    public final int crystal;
    public final int deuterium;

    public Price( int deuterium, int crystal, int metal ) {
        this.deuterium = deuterium;
        this.crystal = crystal;
        this.metal = metal;
    }
}
