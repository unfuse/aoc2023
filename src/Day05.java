import util.InputUtil;
import util.Pair;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Day05 {
    private static final Pattern SEED_LIST_PATTERN = Pattern.compile("^seeds: (.+)\n\n((?>.|\n)+)$");
    private static final Pattern ALMANAC_MAP_PATTERN = Pattern.compile("(\\w+)-to-(\\w+) map:\n((?>.|\n)+)");
    private static final Pattern ENTRY_PATTERN = Pattern.compile("^(\\d+)\\s+(\\d+)\\s+(\\d+)$");

    private Day05() {
    }

    public static void main(final String[] args) {
        final Pair<List<Long>, Almanac> input = parseInput(InputUtil.file("test"));

        part1(input.right(), input.left());
        part2_2(input.right(), input.left());
    }

    public static void part1(final Almanac almanac, final List<Long> seeds) {
        final Pair<Long, Map<Type, Long>> solution = seeds
                .stream()
                .map(seed -> new Pair<>(seed, testSeed(almanac, seed)))
                .min(Comparator.comparingLong(p -> p.right().get(Type.LOCATION)))
                .orElseThrow();

        printSolution(solution.right());

        System.out.println("Part 1: " + solution.right().get(Type.LOCATION));
    }

    public static void part2_1(final Almanac almanac, final List<Long> seeds) {
        final List<Range> ranges = IntStream.iterate(0, i -> i < seeds.size() - 1, i -> i + 2)
                .mapToObj(i -> new Range(seeds.get(i), seeds.get(i) + seeds.get(i + 1)))
                .toList();

        final long start = System.currentTimeMillis();

        final Pair<Long, Map<Type, Long>> solution = ranges
                .stream()
                .flatMap(range -> LongStream.range(range.start, range.end).boxed())
                .distinct()
                .parallel()
                .map(seed -> new Pair<>(seed, testSeed(almanac, seed)))
                .min(Comparator.comparingLong(p -> p.right().get(Type.LOCATION)))
                .orElseThrow();

        final long end = System.currentTimeMillis();
        final long numMinutes = Duration.ofMillis(end - start).toMinutes();

        printSolution(solution.right());

        System.out.printf("Part 2 (in %d minutes): %s%n", numMinutes, solution.right().get(Type.LOCATION));
    }

    public static void part2_2(final Almanac almanac, final List<Long> seeds) {
        final List<Range> ranges = IntStream.iterate(0, i -> i < seeds.size() - 1, i -> i + 2)
                .mapToObj(i -> new Range(seeds.get(i), seeds.get(i) + seeds.get(i + 1)))
                .toList();

        final long start = System.currentTimeMillis();

        final var solution = ranges
                .stream()
                .map(seed -> new Pair<>(seed, testSeedRange(almanac, seed)))
                .mapToLong(p -> p.right().start)
                .min()
                .orElseThrow();

        final long end = System.currentTimeMillis();
        final long numMinutes = Duration.ofMillis(end - start).toMinutes();

        System.out.printf("Part 2 (in %d minutes): %s%n", numMinutes, solution);
    }

    // Parse input
    static Pair<List<Long>, Almanac> parseInput(final String input) {
        final var stuff = SEED_LIST_PATTERN.matcher(input).results().findFirst().orElseThrow();
        final List<Long> seeds = InputUtil.splitStream(stuff.group(1), "\\s").map(Long::parseLong).toList();
        final Almanac almanac = parseAlmanac(stuff.group(2));

        return new Pair<>(seeds, almanac);
    }

    // Parse Almanac
    static Almanac parseAlmanac(final String input) {
        return InputUtil.splitStream(input, "\\n\\n")
                .map(Day05::parseAMap)
                .collect(collectingAndThen(toList(), Almanac::from));
    }

    // Parse Almanac Maps
    static AMap parseAMap(final String input) {
        final var stuff = ALMANAC_MAP_PATTERN.matcher(input).results().findFirst().orElseThrow();
        final Type srcType = Type.valueOf(stuff.group(1).toUpperCase());
        // final Type destType = Type.valueOf(stuff.group(2).toUpperCase());

        final Collection<Entry> entries = stuff.group(3).lines()
                .map(Day05::parseEntry)
                .toList();

        return AMap.from(srcType, entries);
    }

    // Parse Almanac Entries
    static Entry parseEntry(final String input) {
        final var stuff = ENTRY_PATTERN.matcher(input).results().findFirst().orElseThrow();
        return Entry.from(
                Long.parseLong(stuff.group(1)),
                Long.parseLong(stuff.group(2)),
                Long.parseLong(stuff.group(3)));
    }

    // Perform a seed test
    static Map<Type, Long> testSeed(final Almanac almanac, final long test) {
        final Map<Type, Long> res = new EnumMap<>(Type.class);
        res.put(Type.SEED, test);
        testRecur(almanac, Type.SEED, test, res);
        return res;
    }

    // Recursive helper
    private static void testRecur(final Almanac almanac,
                                  final Type curType,
                                  final long test,
                                  final Map<Type, Long> res) {
        if (curType.hasNext()) {
            final Type next = curType.next();
            final long val = almanac.eval(curType, test);
            res.put(next, val);
            testRecur(almanac, next, val, res);
        }
    }

    static Range testSeedRange(final Almanac almanac, final Range range) {
        final var res = testRangeRecur(almanac, Type.SEED, range);
        return res.stream().sorted().findFirst().orElseThrow();
    }

    private static Collection<Range> testRangeRecur(final Almanac almanac, final Type curType, final Range range) {
        if (curType.hasNext()) {
            final Type next = curType.next();
            // The parameter range may intersect one-to-many ranges in the entry list, including void ranges
            return almanac
                    .almanacMaps
                    .get(curType)
                    .bisect(range)
                    .stream()
                    .flatMap(r -> testRangeRecur(almanac, next, r).stream())
                    .collect(toSet());
        }
        return Set.of(range);
    }

    // Pretty print
    static void printSolution(final Map<Type, Long> solution) {
        System.out.println(Stream.of(Type.values())
                .map(t -> "%s %d".formatted(t, solution.get(t)))
                .collect(joining(", ")));
    }

    /**
     * Almanac of tables keyed by their SRC type
     */
    record Almanac(Map<Type, AMap> almanacMaps) {

        long eval(final Type type, final long test) {
            return this.almanacMaps.get(type).eval(test);
        }

        static Almanac from(final Collection<AMap> almanacMaps) {
            return new Almanac(almanacMaps.stream().collect(toMap(AMap::srcType, Function.identity())));
        }
    }

    /**
     * Almanac Map with entries cached for optimal lookup
     */
    record AMap(Type srcType, Collection<Entry> entryBySrc) {

        long eval(final long test) {
            return this.entryBySrc.stream()
                    .filter(e -> e.test(test))
                    .findFirst()
                    .map(e -> e.eval(test))
                    .orElse(test);
        }

        Collection<Range> bisect(final Range range) {
            final Collection<Range> toBeEaten = new HashSet<>();
            toBeEaten.add(range);

            final Collection<Range> eaten = new HashSet<>();

            for (final Entry entry : this.entryBySrc) {
                final Collection<Range> toModify = toBeEaten.stream()
                        .filter(entry::overlapSrc)
                        .collect(toSet());

                final Collection<Range> replacements = toModify.stream()
                        .<Range>mapMulti((r, acc) -> {
                            final Range intersect = r.intersect(entry.src);
                            eaten.add(new Range(entry.eval(intersect.start), entry.eval(intersect.end)));
                            r.bisect(intersect).forEach(acc);
                        })
                        .collect(toSet());

                toBeEaten.removeAll(toModify);
                toBeEaten.addAll(replacements);
            }

            return Stream.concat(eaten.stream(), toBeEaten.stream()).collect(toSet());
        }

        static AMap from(final Type srcType, final Collection<Entry> entries) {
            return new AMap(srcType, entries.stream().sorted(Comparator.comparing(Entry::src)).toList()
            );
        }
    }

    /**
     * Almanac entry with src and dest ranges
     */
    record Entry(Range dest, Range src, long length) {

        boolean test(final long test) {
            return this.src.isValid(test);
        }

        long eval(final long test) {
            return this.dest.start + test - this.src.start;
        }

        boolean overlapSrc(final Range range) {
            return this.src.overlaps(range);
        }

        static Entry from(final long dest, final long src, final long length) {
            return new Entry(new Range(dest, dest + length), new Range(src, src + length), length);
        }
    }

    /**
     * Description of an inclusive start to exclusive range object for use in Almanac Entries
     */
    record Range(long start, long end)
            implements Comparable<Range> {

        boolean isValid(final long test) {
            return test >= this.start && test < this.end;
        }

        boolean overlaps(final Range range) {
            return this.start < range.end && this.end > range.start;
        }

        boolean contains(final Range range) {
            return this.start < range.start && this.end > range.end;
        }

        Range intersect(final Range range) {
            return new Range(Math.max(this.start, range.start), Math.min(this.end, range.end));
        }

        Collection<Range> bisect(final Range range) {
            if (equals(range)) {
                return Collections.emptySet();
            }

            if (contains(range)) {
                return Set.of(
                        new Range(this.start, range.start),
                        new Range(range.end, this.end)
                             );
            }

            return Set.of(range.intersect(this));
        }

        @Override
        public int compareTo(final Range o) {
            return Long.compare(this.start, o.start);
        }
    }

    enum Type {
        SEED,
        SOIL,
        FERTILIZER,
        WATER,
        LIGHT,
        TEMPERATURE,
        HUMIDITY,
        LOCATION;

        Type next() {
            return switch (this) {
                case SEED -> SOIL;
                case SOIL -> FERTILIZER;
                case FERTILIZER -> WATER;
                case WATER -> LIGHT;
                case LIGHT -> TEMPERATURE;
                case TEMPERATURE -> HUMIDITY;
                case HUMIDITY -> LOCATION;
                case LOCATION -> null;
            };
        }

        boolean hasNext() {
            return LOCATION != this;
        }
    }
}
