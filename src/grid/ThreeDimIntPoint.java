package grid;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

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
    public ThreeDimIntPoint plus(final Point<Integer> point) {
        return op(point, Math::addExact);
    }

    @Override
    public ThreeDimIntPoint minus(final Point<Integer> point) {
        return op(point, Math::subtractExact);
    }

    @Override
    public ThreeDimIntPoint op(final Point<Integer> point, final BinaryOperator<Integer> operator) {
        return new ThreeDimIntPoint(
                operator.apply(this.x, point.getX()),
                operator.apply(this.y, point.getY()),
                operator.apply(this.z, point.getZ())
        );
    }

    @Override
    public Stream<Integer> stream() {
        return Stream.of(this.x, this.y, this.z);
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
    public int compareTo(final ThreeDimIntPoint o) {
        return Comparator
                .comparingInt(ThreeDimIntPoint::x)
                .thenComparingInt(ThreeDimIntPoint::y)
                .thenComparingInt(ThreeDimIntPoint::z)
                .compare(this, o);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final ThreeDimIntPoint that)) {
            return false;
        }
        return this.x == that.x && this.y == that.y && this.z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y, this.z);
    }
}
