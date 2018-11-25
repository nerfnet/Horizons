package net.havocmc.service.island;

/**
 * Created by Giovanni on 09/06/2018.
 */
public class IslandLevel {

    /* GLOBAL */
    public static final int INCREMENT = 500;
    public static final int GEMS_REWARD = 5;

    private int current = 0;
    private int experience = 0;

    public int get() {
        return current;
    }

    /**
     * Adds experience to the current island level.
     *
     * @return boolean Whether the island has levelled up or not.
     */
    public boolean addExperience(int amount) {
        experience += amount;
        // We can add more EXP, just don't level the island up.
        if (current == 50)
            return false;

        if (experience >= calculateExpOf(current + 1)) {
            current++;
            return true;
        }

        return false;
    }

    public int calculateExpOf(int level) {
        return 1000 + ((level - 1) * 500);
    }

    public int getExperience() {
        return experience;
    }
}
