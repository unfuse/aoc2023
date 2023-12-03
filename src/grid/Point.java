package grid;

/**
 * A Grid point representing some location of dimensionality. X, Y, Z, etc. are used for
 * ease of communication as structures are TYPICALLY in the 3D Euclidean space, but
 * there is no strong assumption this is true for all cases
 *
 * @param <T> Type of the point keys
 */
public interface Point<T> {

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

    static TwoDimIntPoint of(final int x, final int y) {
        return new TwoDimIntPoint(x, y);
    }

    static ThreeDimIntPoint of(final int x, final int y, final int z) {
        return new ThreeDimIntPoint(x, y, z);
    }
}
