import util.InputUtil;
import util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Day06 {

    private static final Pattern PATTERN = Pattern.compile("^Time:(.+)\\nDistance:(.+)$");

    private Day06() {
    }

    public static void main(final String[] args) {
        final var v = PATTERN.matcher(InputUtil.file("day06"))
                .results()
                .flatMap(r -> Stream.of(r.group(1), r.group(2)))
                .map(r -> r.trim().split(" +"))
                .map(ar -> Stream.of(ar).map(Integer::parseInt).toList())
                .toList();

        final var times = v.get(0);
        final var dists = v.get(1);

        final List<Race> races = new ArrayList<>();

        for (int i = 0; i < times.size(); i++) {
            races.add(new Race(times.get(i), dists.get(i)));
        }

        part1(races);
        part2(races);
    }

    public static void part1(final List<Race> races) {
        System.out.println("Part 1: " + races.stream().mapToLong(Race::numWins).reduce(1, (a, b) -> a * b));
    }

    public static void part2(final List<Race> races) {
        final StringBuilder longTime = new StringBuilder(5);
        final StringBuilder longDist = new StringBuilder(5);

        for (final var race : races) {
            longTime.append(race.time);
            longDist.append(race.distance);
        }

        final Race longRace = new Race(Long.parseLong(longTime.toString()), Long.parseLong(longDist.toString()));

        System.out.println("Part 2: " + longRace.numWins());
    }

    record Race(long time, long distance) {
        Pair<Long, Long> getBest() {
            final long idx = (long) Math.floor(this.time / 2.0);
            final long dist = idx * (this.time - idx);
            return new Pair<>(idx, dist);
        }

        long numWins() {
            final var best = getBest();

            // sanity check
            if (best.right() < this.distance) {
                return 0;
            }

            final long numWinsLeft = LongStream.iterate(best.left(), i -> i >= 1, i -> i - 1)
                    .takeWhile(i -> (i * (this.time - i)) > this.distance)
                    .count();

            if ((this.time % 2) == 0) {
                return ((numWinsLeft - 1) * 2) + 1;
            }

            return (numWinsLeft * 2);
        }
    }
}
