package grid;

import java.util.Comparator;

public record ThreeDimIntPoint(int x, int y, int z)
        implements Point<Integer>, Comparable<ThreeDimIntPoint> {
    @Override
    public Integer getX() {
        return this.x;
    }

    @Override
    public Integer getY() {
        return this.y;
    }

    @Override
    public Integer getZ() {
        return this.z;
    }

    @Override
    public int compareTo(final ThreeDimIntPoint o) {
        return Comparator
                .comparingInt(ThreeDimIntPoint::x)
                .thenComparingInt(ThreeDimIntPoint::y)
                .thenComparingInt(ThreeDimIntPoint::z)
                .compare(this, o);
    }
}
