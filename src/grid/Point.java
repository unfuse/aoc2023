package grid;

import java.util.Iterator;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

/**
 * A Grid point representing some location of dimensionality. X, Y, Z, etc. are used for
 * ease of communication as structures are TYPICALLY in the 3D Euclidean space, but
 * there is no strong assumption this is true for all cases
 *
 * @param <T> Type of the point keys
 */
public interface Point<T>
        extends Iterable<T> {

    /**
     * @return Value of the first dimension
     */
    T getX();

    /**
     * @return Value of the second dimension
     */
    T getY();

    /**
     * @return Value of the third dimension
     */
    T getZ();

    /**
     * @return Resulting Point of adding every dimension of this point and another {@code point} together
     */
    Point<T> plus(Point<T> point);

    /**
     * @return Resulting Point of subtracting every dimension of this point and another {@code point} together
     */
    Point<T> minus(Point<T> point);

    /**
     * @return Resulting Point of operating on every dimension of this point and another {@code point} in pairs
     */
    Point<T> op(Point<T> point, BinaryOperator<T> operator);

    /**
     * @return is parameter {@code point} distance one from this point varying in only a single dimension
     */
    boolean isCardinal(Point<T> point);

    /**
     * @return is parameter {@code point} distance one from the point, including in all dimensions
     */
    boolean isOrthogonal(Point<T> point);

    /**
     * @return A Stream of the dimensions of this Point
     */
    Stream<T> stream();

    /**
     * @return an Iterator of the dimensions of this Point
     */
    @Override
    default Iterator<T> iterator() {
        return stream().iterator();
    }

    static TwoDimIntPoint of(final int x, final int y) {
        return new TwoDimIntPoint(x, y);
    }

    static ThreeDimIntPoint of(final int x, final int y, final int z) {
        return new ThreeDimIntPoint(x, y, z);
    }
}
