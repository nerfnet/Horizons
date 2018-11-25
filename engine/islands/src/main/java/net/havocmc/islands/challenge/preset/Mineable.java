package net.havocmc.islands.challenge.preset;

/**
 * Created by Giovanni on 09/06/2018.
 */
public enum Mineable {

    REDSTONE("REDSTONE_ORE", "REDSTONE ORE"),
    GOLD("GOLD_ORE", "GOLD ORE"),
    DIAMOND("DIAMOND_ORE", "DIAMOND ORE");

    private String rawMaterialName;
    private String defaultName;

    Mineable(String rawMaterialName, String defaultName) {
        this.rawMaterialName = rawMaterialName;
        this.defaultName = defaultName;
    }

    public String getRawMaterialName() {
        return rawMaterialName;
    }

    public String getDefaultName() {
        return defaultName;
    }
}
