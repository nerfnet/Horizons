package net.havocmc.islands.vector;

import java.io.Serializable;

/**
 * Created by Giovanni on 06/04/2018.
 */
public class IslandVector implements Serializable {

    private int x;
    private int y;
    private int z;

    public IslandVector(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }
}
