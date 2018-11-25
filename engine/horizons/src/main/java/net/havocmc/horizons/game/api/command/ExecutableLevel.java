package net.havocmc.horizons.game.api.command;

/**
 * Created by Giovanni on 25/03/2018.
 */
public enum ExecutableLevel {

    PLAYER(0),
    OPERATOR(1),
    CONSOLE(2);

    private int weight;


    ExecutableLevel(int weight) {
        this.weight = weight;
    }

    public boolean atLevel(ExecutableLevel level) {
        return weight >= level.getWeight();
    }

    public int getWeight() {
        return weight;
    }
}
