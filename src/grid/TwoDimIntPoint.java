package grid;

import java.util.Comparator;

public record TwoDimIntPoint(int x, int y)
        implements Point<Integer>, Comparable<TwoDimIntPoint> {
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
        return 0;
    }

    @Override
    public int compareTo(final TwoDimIntPoint o) {
        return Comparator
                .comparingInt(TwoDimIntPoint::x)
                .thenComparingInt(TwoDimIntPoint::y)
                .compare(this, o);
    }
}
