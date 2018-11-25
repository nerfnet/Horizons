package net.havocmc.islands.challenge.preset;

/**
 * Created by Giovanni on 09/06/2018.
 */
public enum Farmable {

    PUMPKIN("PUMPKIN", "PUMPKINS"),
    MELON("MELON_BLOCK", "MELONS"),
    WHEAT("CROPS", "WHEAT"),
    CACTUS("CACTUS", "CACTI"),
    COCO_BEAN("COCOA", "COCO BEANS"),
    SUGAR_CANE("SUGAR_CANE_BLOCK", "SUGAR CANE");

    private String rawMaterialName;
    private String defaultName;

    Farmable(String rawMaterialName, String defaultName) {
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
