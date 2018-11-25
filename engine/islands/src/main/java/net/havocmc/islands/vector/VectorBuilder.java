package net.havocmc.islands.vector;

/**
 * Created by Giovanni on 06/04/2018.
 */
public interface VectorBuilder<E> {

    IslandVector toIslandVector();

    /**
     * Unwraps this {@link VectorBuilder<E>} back to the origin state.
     */
    E unwrapVector();
}
