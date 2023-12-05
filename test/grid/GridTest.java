package grid;

import util.Pair;

import java.util.Objects;

import org.junit.jupiter.api.Test;

public class GridTest {

    private static final String TWO_DIM_TEST = """
            0 0 a
            1 2 b
            -5 3 c
            """;

    @Test
    void testTwoDim() {
        final TwoDimIntGrid<String> grid = TWO_DIM_TEST
                .lines()
                .map(line -> {
                    final String[] split = line.trim().split("\\s");
                    return new Pair<>(
                            Point.of(
                                    Integer.parseInt(split[0]),
                                    Integer.parseInt(split[1])),
                            split[2]);
                })
                .collect(TwoDimIntGrid.pairCollector());

        assert Objects.nonNull(grid);
        assert grid.size() == 3;
        assert Objects.equals(grid.get(0, 0), "a");
        assert Objects.equals(grid.get(1, 2), "b");
        assert Objects.equals(grid.get(-5, 3), "c");
        assert Objects.isNull(grid.get(4, 3));
        assert grid.getMinX() == -5;
        assert grid.getMaxX() == 1;
        assert grid.getMinY() == 0;
        assert grid.getMaxY() == 3;
        assert grid.keySet().stream().flatMap(TwoDimIntPoint::stream).anyMatch(dim -> dim > -6);
        assert !grid.keySet().stream().flatMap(TwoDimIntPoint::stream).allMatch(dim -> dim > 0);
    }
}
