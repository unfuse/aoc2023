package grid;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

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
    public TwoDimIntPoint plus(final Point<Integer> point) {
        return op(point, Math::addExact);
    }

    @Override
    public TwoDimIntPoint minus(final Point<Integer> point) {
        return op(point, Math::subtractExact);
    }

    @Override
    public TwoDimIntPoint op(final Point<Integer> point, final BinaryOperator<Integer> operator) {
        return new TwoDimIntPoint(
                operator.apply(this.x, point.getX()),
                operator.apply(this.y, point.getY())
        );
    }

    @Override
    public Stream<Integer> stream() {
        return Stream.of(this.x, this.y);
    }

    @Override
    public boolean isCardinal(final Point<Integer> point) {
        return PointUtils.isCardinal(this, point);
    }

    @Override
    public boolean isOrthogonal(final Point<Integer> point) {
        return PointUtils.isOrthogonal(this, point);
    }

    @Override
    public int compareTo(final TwoDimIntPoint o) {
        return Comparator
                .comparingInt(TwoDimIntPoint::y)
                .thenComparingInt(TwoDimIntPoint::x)
                .compare(this, o);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final TwoDimIntPoint that)) {
            return false;
        }
        return this.x == that.x && this.y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }
}
