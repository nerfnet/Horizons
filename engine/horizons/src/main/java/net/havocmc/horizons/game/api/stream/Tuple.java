package net.havocmc.horizons.game.api.stream;

/**
 * Created by Giovanni on 10/06/2018.
 *
 * Simple immutable Tuple so we don't have to use {@link net.minecraft.server.v1_9_R1.Tuple}.
 */
public class Tuple<A, B> {

    private final A a;
    private final B b;

    public Tuple(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A a() {
        return a;
    }

    public B b() {
        return b;
    }
}
