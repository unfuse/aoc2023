package grid;

import java.util.Map;
import java.util.Optional;

/**
 * Structure of abstract Points referring to abstract Nodes in an infinite space
 * @param <P> type of Point
 * @param <N> type of Node
 */
public interface Grid<T, P extends Point<T> & Comparable<P>, N>
        extends Map<P, N> {

    /**
     * @return a P expressing the maximum values of the Grid space
     */
    P getMaxRangePoint();

    /**
     * @return a P expressing the minimum values of the Grid space
     */
    P getMinRangePoint();

    /**
     * @return minimum first dimension value
     */
    T getMinX();

    /**
     * @return maximum first dimension value
     */
    T getMaxX();

    /**
     * @return minimum second dimension value
     */
    T getMinY();

    /**
     * @return maximum second dimension value
     */
    T getMaxY();

    /**
     * @return minimum third dimension value
     */
    T getMinZ();

    /**
     * @return maximum third dimension value
     */
    T getMaxZ();

    /**
     * @return a non-null Optional wrapper around {@link #get(Object)}
     */
    default Optional<N> maybeGet(final P point) {
        return Optional.ofNullable(get(point));
    }

    /**
     * Utility entry-like class to support pairing a {@code point} and a {@code node}.
     * Intended to help create Grids, because {@link Map#entry(Object, Object)} does not permit null values
     */
    record Pair<T, P extends Point<T> & Comparable<P>, N>(P point, N value) {
    }
}
