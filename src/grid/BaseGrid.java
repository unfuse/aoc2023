package grid;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collector;

public abstract class BaseGrid<T, P extends Point<T> & Comparable<P>, N>
        extends DelegateMap<P, N>
        implements Grid<T, P, N> {

    protected BaseGrid() {
    }

    protected BaseGrid(final Map<P, N> delegateMap) {
        super(delegateMap);
    }

    protected static <P extends Point<?> & Comparable<P>, N, G extends Grid<?, P, N>> Collector<P, ?, G> createCollector(
            final Supplier<G> ctor,
            final N defaultNode) {
        return Collector.of(ctor,
                (acc, point) -> acc.put(point, defaultNode),
                (g1, g2) -> {
                    g1.putAll(g2);
                    return g1;
                });
    }

    protected static <T, P extends Point<T> & Comparable<P>, N, G extends Grid<T, P, N>> Collector<Grid.Pair<T, P, N>, ?, G> createCollector(
            final Supplier<G> ctor) {
        return Collector.of(ctor,
                (acc, pair) -> acc.put(pair.point(), pair.value()),
                (g1, g2) -> {
                    g1.putAll(g2);
                    return g1;
                });
    }
}
