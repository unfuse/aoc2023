package grid;

import java.util.Optional;
import java.util.stream.Collector;

public class TwoDimIntGrid<N>
        extends BaseGrid<Integer, TwoDimIntPoint, N> {

    private int minX, maxX, minY, maxY;

    private TwoDimIntGrid() {
    }

    @Override
    public TwoDimIntPoint getMaxRangePoint() {
        return new TwoDimIntPoint(this.maxX, this.maxY);
    }

    @Override
    public TwoDimIntPoint getMinRangePoint() {
        return new TwoDimIntPoint(this.minX, this.minY);
    }

    @Override
    public Integer getMaxX() {
        return this.maxX;
    }

    @Override
    public Integer getMinX() {
        return this.minX;
    }

    @Override
    public Integer getMinY() {
        return this.minY;
    }

    @Override
    public Integer getMaxY() {
        return this.maxY;
    }

    @Override
    public Integer getMinZ() {
        return 0;
    }

    @Override
    public Integer getMaxZ() {
        return 0;
    }

    @Override
    public N put(final TwoDimIntPoint key, final N value) {
        final N node = super.put(key, value);
        this.minX = Math.min(key.x(), this.minX);
        this.maxX = Math.max(key.x(), this.maxX);
        this.minY = Math.min(key.y(), this.minY);
        this.maxY = Math.max(key.y(), this.maxY);
        return node;
    }

    public N put(final int x, final int y, final N value) {
        return put(new TwoDimIntPoint(x, y), value);
    }

    public Optional<N> maybeGet(final int x, final int y) {
        return maybeGet(new TwoDimIntPoint(x, y));
    }

    public N get(final int x, final int y) {
        return get(new TwoDimIntPoint(x, y));
    }

    public static <N> TwoDimIntGrid<N> of() {
        return new TwoDimIntGrid<>();
    }

    public static <N> Collector<TwoDimIntPoint, ?, TwoDimIntGrid<N>> emptyCollector() {
        return defaultCollector(null);
    }

    public static <N> Collector<TwoDimIntPoint, ?, TwoDimIntGrid<N>> defaultCollector(final N defaultNode) {
        return createCollector(TwoDimIntGrid::new, defaultNode);
    }

    public static <N> Collector<Grid.Pair<Integer, TwoDimIntPoint, N>, ?, TwoDimIntGrid<N>> pairCollector() {
        return createCollector(TwoDimIntGrid::new);
    }
}
