import grid.Grid;
import grid.Point;
import grid.TwoDimIntGrid;
import grid.TwoDimIntPoint;
import util.InputUtil;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.flatMapping;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class Day03 {

    private Day03() {
    }

    public static void main(final String[] args) {
        // Prepare and cache useful grid structures
        final TwoDimIntGrid<String> grid = getGrid(InputUtil.lines("day03"));
        final Set<WideNumber> wideNumbers = groupWideNumbers(grid);
        final Set<TwoDimIntPoint> charPoints = getCharPoints(grid);
        final Set<WideNumber> partNumbers = getPartNumbers(wideNumbers, charPoints);

        // do parts
        part1(grid, partNumbers);
        part2(grid, charPoints, partNumbers);
    }

    public static void part1(final TwoDimIntGrid<String> grid,
                             final Set<WideNumber> partNumbers) {
        final int sum = partNumbers
                .stream()
                .mapToInt(wn -> wn.getValue(grid))
                .sum();

        System.out.println("Part 1: " + sum);
    }

    public static void part2(final TwoDimIntGrid<String> grid,
                             final Set<TwoDimIntPoint> charPoints,
                             final Set<WideNumber> partNumbers) {
        // Find gears
        final Set<TwoDimIntPoint> gearPoints = charPoints
                .stream()
                .filter(point -> grid.get(point).equals("*"))
                .collect(toSet());

        // Group parts by gear conditions and calculate their ratios
        final int gearRatioSum = gearPoints
                .stream()
                .map(gear -> partNumbers
                        .stream()
                        .filter(wn -> wn.points
                                .stream()
                                .anyMatch(gear::isOrthogonal))
                        .collect(toSet()))
                .filter(wns -> wns.size() == 2)
                .mapToInt(wns -> wns
                        .stream()
                        .mapToInt(wn -> wn.getValue(grid))
                        .reduce(1, (a, b) -> a * b))
                .sum();

        System.out.println("Part 2: " + gearRatioSum);
    }

    // Build two dim grid from input
    // the line iteration correlates to the y-axis, and parts of the line correlate to the x-axis
    private static TwoDimIntGrid<String> getGrid(final List<String> lines) {
        final var y = new AtomicInteger(0);
        return lines
                .stream()
                .flatMap(line -> {
                    final var x = new AtomicInteger();
                    final var newLine = Stream.of(line.split(""))
                            .map(cell -> new Grid.Pair<>(Point.of(x.getAndIncrement(), y.get()), cell))
                            .collect(toSet());
                    y.getAndIncrement();
                    return newLine.stream();
                })
                .collect(TwoDimIntGrid.pairCollector());
    }

    // identify consecutive numbers within the schematic by their cell Points
    private static Set<WideNumber> groupWideNumbers(final TwoDimIntGrid<String> grid) {
        return grid
                .keySet()
                .stream()
                .collect(groupingBy(TwoDimIntPoint::y))
                .values()
                .stream()
                .collect(flatMapping(
                        rowPoints -> rowPoints
                                .stream()
                                .sorted() // WE -MUST- HAVE A CANONICALLY SORTED STREAM HERE
                                .collect(new ConsecutiveNumberCollector(grid)) // Collect consecutive numbers in place
                                .stream(),
                        toSet()));
    }

    // find non-digit, non-blank cells to use for identifying part numbers
    private static Set<TwoDimIntPoint> getCharPoints(final TwoDimIntGrid<String> grid) {
        return grid
                .entrySet()
                .stream()
                .filter(entry -> !entry.getValue().matches("\\d|\\."))
                .map(Map.Entry::getKey)
                .collect(toSet());
    }

    // derive which numbers in the schematic qualify as part numbers
    private static Set<WideNumber> getPartNumbers(final Set<WideNumber> wideNumbers,
                                                  final Set<TwoDimIntPoint> charPoints) {
        return wideNumbers
                .stream()
                .filter(wn -> wn.points
                        .stream()
                        // This will loop an excessive amount of times - a potentially more efficient approach
                        // would be for every point to output their "orthogonal set" and check if any of those are
                        // in the `charPoint` set
                        .anyMatch(point -> charPoints
                                .stream()
                                .anyMatch(point::isOrthogonal)))
                .collect(toSet());
    }

    // a consecutive chain of points within the schematic that qualifies as a number
    record WideNumber(Set<TwoDimIntPoint> points) {
        int getValue(final TwoDimIntGrid<String> grid) {
            return this.points
                    .stream()
                    .sorted()
                    .map(grid::get)
                    .collect(collectingAndThen(joining(), Integer::parseInt));
        }
    }

    // A stateful Collector that assigns an incrementing grouping ID to every Point
    // The grouping ID increments when it encounters a non-digit cell
    // This collector relies on the encounter order being the proper left-to-right read order of lines of the Grid
    private static class ConsecutiveNumberCollector
            implements Collector<TwoDimIntPoint, Set<Map.Entry<Integer, TwoDimIntPoint>>, Set<WideNumber>> {

        private final TwoDimIntGrid<String> grid;
        private final AtomicInteger idx;

        private ConsecutiveNumberCollector(final TwoDimIntGrid<String> grid) {
            this.grid = grid;
            this.idx = new AtomicInteger();
        }

        // store intermediate results in a hash set - ez
        @Override
        public Supplier<Set<Map.Entry<Integer, TwoDimIntPoint>>> supplier() {
            return HashSet::new;
        }

        // for digit cells, pair the cell with it's grouping id
        // else skip the cell and increment the grouping id
        @Override
        public BiConsumer<Set<Map.Entry<Integer, TwoDimIntPoint>>, TwoDimIntPoint> accumulator() {
            return (acc, point) -> {
                final String val = this.grid.get(point);
                if (!val.matches("\\d")) {
                    this.idx.getAndIncrement();
                    return;
                }

                acc.add(Map.entry(this.idx.get(), point));
            };
        }

        // sets can collapse on each other - ez
        @Override
        public BinaryOperator<Set<Map.Entry<Integer, TwoDimIntPoint>>> combiner() {
            return (o1, o2) -> {
                o1.addAll(o2);
                return o1;
            };
        }

        // a stylistic flair to turn the intermediate state of Set<pairs> into Set<WideNumber>
        // so the output of this one collector is more useful to the code context immediately
        @Override
        public Function<Set<Map.Entry<Integer, TwoDimIntPoint>>, Set<WideNumber>> finisher() {
            return set -> set
                    .stream()
                    .collect(collectingAndThen(
                            groupingBy(Map.Entry::getKey,
                                    mapping(Map.Entry::getValue, toSet())),
                            map -> map
                                    .values()
                                    .stream()
                                    .map(WideNumber::new)
                                    .collect(toSet())));
        }

        // does not qualify for any special characteristics
        @Override
        public Set<Characteristics> characteristics() {
            return Set.of();
        }
    }
}
