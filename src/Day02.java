import util.InputUtil;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public class Day02 {

    private static final String RED = "red";
    private static final String GREEN = "green";
    private static final String BLUE = "blue";

    private static final Map<String, Integer> STARTING_DRAW_MAP = Map.of(
            RED, 0,
            GREEN, 0,
            BLUE, 0);

    private Day02() {
    }

    public static void main(final String[] args) {
        final Map<Integer, Game> games = InputUtil.lines("day02")
                .stream()
                .map(Day02::processGame)
                .collect(toMap(Game::id, Function.identity()));

        part1(games);
        part2(games);
    }

    public static void part1(final Map<Integer, Game> games) {
        final Map<String, Integer> validBallCriteria = Map.of(
                RED, 12,
                GREEN, 13,
                BLUE, 14);

        final int sum = games
                .entrySet()
                .stream()
                .filter(gameEntry -> gameEntry.getValue().rounds
                        .stream()
                        .allMatch(round -> round.draws
                                .entrySet()
                                .stream()
                                .allMatch(entry -> validBallCriteria.get(entry.getKey()) >= entry.getValue())))
                .mapToInt(Map.Entry::getKey)
                .sum();

        System.out.println("Valid game sum part 1: " + sum);
    }

    public static void part2(final Map<Integer, Game> games) {
        final int sum = games
                .values()
                .stream()
                .mapToInt(game -> {
                    final Map<String, Integer> lowestBalls = new HashMap<>(STARTING_DRAW_MAP);
                    game.rounds.forEach(round ->
                            round.draws.forEach((color, qty) ->
                                    lowestBalls.compute(color, (key, oldQty) -> Math.max(oldQty, qty))));
                    return lowestBalls
                            .values()
                            .stream()
                            .reduce(1, (a, b) -> a * b);
                })
                .sum();

        System.out.println("Valid game sum part 2: " + sum);
    }

    private static Game processGame(final String line) {
        // Separate `{Game \d} : {rounds}`
        final String[] split = line.split(":");

        // Separate `{Game} {\d}`
        final String[] headerSplit = split[0].trim().split("\\s");

        // Parse Game ID
        final int gameId = Integer.parseInt(headerSplit[1]);

        // Split `{round}; {round};` etc
        // Process into rounds
        final Set<Round> rounds = Stream.of(split[1].split(";"))
                .map(Day02::processRound)
                .collect(toSet());

        return new Game(gameId, rounds);
    }

    private static Round processRound(final String round) {
        final Map<String, Integer> draws = new HashMap<>(STARTING_DRAW_MAP);

        // Split `{draw}, {draw},` etc
        Stream.of(round.split(","))
                .map(draw -> {
                    // Split `{draw amount} {draw color}`
                    final String[] drawSplit = draw.trim().split("\\s");
                    return Map.entry(drawSplit[1].trim(), Integer.parseInt(drawSplit[0].trim()));
                })
                .forEach(entry -> draws.put(entry.getKey(), entry.getValue()));

        return new Round(draws);
    }

    record Game(int id, Set<Round> rounds) {
    }

    record Round(Map<String, Integer> draws) {
    }
}
