import util.InputUtil;
import util.Pair;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.time.Duration;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
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
        final Pair<List<Long>, Almanac> input = parseInput(InputUtil.file("day05"));

        part1(input.right(), input.left());
        part2(input.right(), input.left());
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

    public static void part2(final Almanac almanac, final List<Long> seeds) {
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

    static Pair<List<Long>, Almanac> parseInput(final String input) {
        final var stuff = SEED_LIST_PATTERN.matcher(input).results().findFirst().orElseThrow();
        final List<Long> seeds = InputUtil.splitStream(stuff.group(1), "\\s").map(Long::parseLong).toList();
        final Almanac almanac = parseAlmanac(stuff.group(2));

        return new Pair<>(seeds, almanac);
    }

    static Almanac parseAlmanac(final String input) {
        return InputUtil.splitStream(input, "\\n\\n")
                .map(Day05::parseAMap)
                .collect(collectingAndThen(toList(), Almanac::from));
    }

    static AMap parseAMap(final String input) {
        final var stuff = ALMANAC_MAP_PATTERN.matcher(input).results().findFirst().orElseThrow();
        final Type srcType = Type.valueOf(stuff.group(1).toUpperCase());
        final Type destType = Type.valueOf(stuff.group(2).toUpperCase());

        final Collection<Entry> entries = stuff.group(3).lines()
                .map(Day05::parseEntry)
                .toList();

        return AMap.from(srcType, entries);
    }

    static Entry parseEntry(final String input) {
        final var stuff = ENTRY_PATTERN.matcher(input).results().findFirst().orElseThrow();
        return Entry.from(
                Long.parseLong(stuff.group(1)),
                Long.parseLong(stuff.group(2)),
                Long.parseLong(stuff.group(3)));
    }

    static Map<Type, Long> testSeed(final Almanac almanac, final long test) {
        final Map<Type, Long> res = new EnumMap<>(Type.class);
        res.put(Type.SEED, test);
        testRecur(almanac, Type.SEED, test, res);
        return res;
    }

    static void testRecur(final Almanac almanac, final Type curType, final long test, final Map<Type, Long> res) {
        if (curType.hasNext()) {
            final Type next = curType.next();
            final long val = almanac.eval(curType, test);
            res.put(next, val);
            testRecur(almanac, next, val, res);
        }
    }

    static void printSolution(final Map<Type, Long> solution) {
        System.out.println(Stream.of(Type.values())
                .map(t -> "%s %d".formatted(t, solution.get(t)))
                .collect(joining(", ")));
    }

    record Almanac(Map<Type, AMap> almanacMaps) {

        long eval(final Type type, final long test) {
            return this.almanacMaps.get(type).eval(test);
        }

        static Almanac from(final Collection<AMap> almanacMaps) {
            return new Almanac(almanacMaps.stream().collect(toMap(AMap::srcType, Function.identity())));
        }
    }

    record AMap(Type destType, Collection<Entry> entryByDest, Type srcType, Collection<Entry> entryBySrc) {

        long eval(final long test) {
            return this.entryBySrc.stream()
                    .filter(e -> e.test(test))
                    .findFirst()
                    .map(e -> e.eval(test))
                    .orElse(test);
        }

        static AMap from(final Type srcType, final Collection<Entry> entries) {
            return new AMap(srcType.next(), entries.stream().sorted(Comparator.comparing(Entry::dest)).toList(),
                    srcType, entries.stream().sorted(Comparator.comparing(Entry::src)).toList()
            );
        }
    }

    record Entry(Range dest, Range src, long length) {

        boolean test(final long test) {
            return this.src.isValid(test);
        }

        long eval(final long test) {
            return this.dest.start + test - this.src.start;
        }

        static Entry from(final long dest, final long src, final long length) {
            return new Entry(new Range(dest, dest + length), new Range(src, src + length), length);
        }
    }

    record Range(long start, long end)
            implements Comparable<Range> {

        boolean isValid(final long test) {
            return test >= this.start && test < this.end;
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
