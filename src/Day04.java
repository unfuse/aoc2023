import util.InputUtil;
import static java.util.stream.Collectors.toSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

public class Day04 {

    private Day04() {
    }

    public static void main(final String[] args) {
        final List<Game> games = InputUtil.lines("day04")
                .stream()
                .map(line -> {
                    final var splitHeader = InputUtil.splitHalf(line, "\\:");
                    final var splitId = InputUtil.splitHalf(splitHeader.left(), "\\s+");
                    final var splitGame = InputUtil.splitHalf(splitHeader.right(), "\\|");
                    final int gameId = Integer.parseInt(splitId.right());
                    final Set<Integer> winners = InputUtil
                            .splitStream(splitGame.left(), "\\s+")
                            .map(Integer::parseInt)
                            .collect(toSet());
                    final Set<Integer> numbers = InputUtil
                            .splitStream(splitGame.right(), "\\s+")
                            .map(Integer::parseInt)
                            .collect(toSet());
                    return new Game(gameId, winners, numbers);
                })
                .toList();

        part1(games);
        part2(games);
    }

    public static void part1(final List<Game> games) {
        final int score = games
                .stream()
                .map(game -> game.numbers
                        .stream()
                        .filter(game.winners::contains)
                        .count())
                .mapToInt(count -> (int) Math.pow(2, count - 1))
                .sum();

        System.out.println("Part 1: " + score);
    }

    public static void part2(final List<Game> games) {
        // copy map, seeded to 1
        final Map<Integer, Integer> copies = new HashMap<>();
        final int maxId = games
                .stream()
                .mapToInt(game -> {
                    copies.put(game.id, 1);
                    return game.id;
                })
                .max()
                .orElseThrow();

        games.forEach(game -> {
            final int numWinners = (int) game.numbers.stream().filter(game.winners::contains).count();
            if (game.id < maxId && numWinners > 0) {
                IntStream.rangeClosed(game.id + 1, Math.min(game.id + numWinners, maxId))
                        .forEach(i -> copies.compute(i, (key, old) -> old + copies.get(game.id)));
            }
        });

        final int score = copies.values().stream().mapToInt(i -> i).sum();

        System.out.println("Part 2: " + score);
    }

    record Game(int id, Set<Integer> winners, Set<Integer> numbers) {
    }
}
