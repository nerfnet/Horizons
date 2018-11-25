package net.havocmc.islands;

/**
 * Created by Giovanni on 01/03/2018.
 */
public enum IslandRegion {

    REDSTONE_LAIRE("Redstone Laire", 20),
    WINTER_PALACE("Winter Palace", 20),
    BARREN_DESERT("Barren Desert", 20),
    FOREST_COTTAGE("Forest Cottage", 20);

    private String name;
    private int gemPrice;

    IslandRegion(String name, int gemPrice) {
        this.name = name;
        this.gemPrice = gemPrice;
    }

    public String getName() {
        return name;
    }

    public int getGemPrice() {
        return gemPrice;
    }
}
