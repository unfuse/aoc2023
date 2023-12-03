package grid;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;

public class ThreeDimIntGrid<N>
        extends BaseGrid<Integer, ThreeDimIntPoint, N> {

    private int minX, maxX, minY, maxY, minZ, maxZ;

    private ThreeDimIntGrid() {
    }

    private ThreeDimIntGrid(final Map<ThreeDimIntPoint, N> map) {
        super(map);
    }

    @Override
    public ThreeDimIntPoint getMaxRangePoint() {
        return new ThreeDimIntPoint(this.maxX, this.maxY, this.maxZ);
    }

    @Override
    public ThreeDimIntPoint getMinRangePoint() {
        return new ThreeDimIntPoint(this.minX, this.minY, this.minZ);
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
        return this.minZ;
    }

    @Override
    public Integer getMaxZ() {
        return this.maxX;
    }

    @Override
    public N put(final ThreeDimIntPoint key, final N value) {
        final N node = super.put(key, value);
        this.minX = Math.min(key.x(), this.minX);
        this.maxX = Math.max(key.x(), this.maxX);
        this.minY = Math.min(key.y(), this.minY);
        this.maxY = Math.max(key.y(), this.maxY);
        this.minZ = Math.min(key.z(), this.minZ);
        this.maxZ = Math.max(key.z(), this.maxZ);
        return node;
    }

    public N put(final int x, final int y, final int z, final N value) {
        return put(new ThreeDimIntPoint(x, y, z), value);
    }

    public Optional<N> maybeGet(final int x, final int y, final int z) {
        return maybeGet(new ThreeDimIntPoint(x, y, z));
    }

    public N get(final int x, final int y, final int z) {
        return get(new ThreeDimIntPoint(x, y, z));
    }

    public static <N> ThreeDimIntGrid<N> of() {
        return new ThreeDimIntGrid<>();
    }

    public static <N> Collector<ThreeDimIntPoint, ?, ThreeDimIntGrid<N>> emptyCollector() {
        return defaultCollector(null);
    }

    public static <N> Collector<ThreeDimIntPoint, ?, ThreeDimIntGrid<N>> defaultCollector(final N defaultNode) {
        return createCollector(ThreeDimIntGrid::new, defaultNode);
    }

    public static <N> Collector<Pair<Integer, ThreeDimIntPoint, N>, ?, ThreeDimIntGrid<N>> pairCollector() {
        return createCollector(ThreeDimIntGrid::new);
    }
}
