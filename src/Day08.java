import util.InputUtil;
import util.Pair;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day08 {

    private static final Pattern NODE_PATTERN = Pattern.compile("^([\\d\\w]{3}) = \\(([\\d\\w]{3}), ([\\d\\w]{3})\\)$");

    private Day08() {
    }

    public static void main(final String[] args) {
        final String input = InputUtil.file("day08");
        final InputUtil.Split split = InputUtil.splitHalf(input, "\n\n");
        final String path = split.left().trim();
        final Map<String, Node> nodeMap = split.right().trim().lines()
                .map(Day08::parseNode)
                .collect(Collectors.toMap(Node::key, Function.identity()));

        part1(nodeMap, path);
        part2(nodeMap, path);
    }

    public static void part1(final Map<String, Node> nodeMap, final String path) {
        final State state = new State("AAA", str -> str.equals("ZZZ"));
        for (final String dir : new InfiniteIterator(path)) {
            step(nodeMap, state, dir);
            if (state.done()) {
                break;
            }
        }
        System.out.println("Part 1: " + state.count);
    }

    public static void part2(final Map<String, Node> nodeMap, final String path) {
        final Predicate<String> ZENDER = str -> str.endsWith("Z");
        final Set<State> states = nodeMap.keySet().stream()
                .filter(key -> key.endsWith("A"))
                .map(key -> new State(key, ZENDER))
                .collect(Collectors.toSet());
        final int numStates = states.size();

        int laps = 0;
        boolean all_done;
        for (final String dir : new InfiniteIterator(path)) {
            all_done = true;
            int count_done = 0;
            for (final State state : states) {
                step(nodeMap, state, dir);
                all_done &= state.done();
                if (state.done()) {
                    count_done++;
                }
            }
            laps++;
            System.out.printf("  Lap: %d | %d / %d completed%n", laps, count_done, numStates);
            if (all_done) {
                break;
            }
        }

        System.out.println("Part 2: " + states.stream().findFirst().get().count);
    }

    private static Node parseNode(final String line) {
        final Matcher matcher = NODE_PATTERN.matcher(line);

        if (!matcher.matches()) {
            throw new IllegalStateException(line);
        }

        final MatchResult matchResult = matcher.toMatchResult();

        return new Node(matchResult.group(1), new Pair<>(matchResult.group(2), matchResult.group(3)));
    }

    private static void step(final Map<String, Node> nodeMap,
                             final State state,
                             final String dir) {
        final Node curNode = nodeMap.get(state.currentKey);
        final String nextKey = nextKey(curNode, dir);
        state.count++;
        state.currentKey = nextKey;
    }

    private static String nextKey(final Node node, final String dir) {
        return switch (dir) {
            case "L" -> node.paths.left();
            case "R" -> node.paths.right();
            default -> throw new IllegalArgumentException("Bad dir: " + dir);
        };
    }

    record Node(String key, Pair<String, String> paths) {
    }

    static class InfiniteIterator
            implements Iterator<String>, Iterable<String> {
        private final String path;
        private volatile Iterator<String> itr;

        InfiniteIterator(final String path) {
            this.path = path;
            this.itr = InputUtil.splitStream(path, "").iterator();
        }

        @Override
        public Iterator<String> iterator() {
            return this;
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public String next() {
            if (!this.itr.hasNext()) {
                this.itr = InputUtil.splitStream(this.path, "").iterator();
            }
            return this.itr.next();
        }
    }

    static class State {
        String currentKey;
        int count;
        final Predicate<String> endCondition;

        State(final String currentKey, final Predicate<String> endCondition) {
            this.currentKey = currentKey;
            this.endCondition = endCondition;
        }

        boolean done() {
            return this.endCondition.test(this.currentKey);
        }
    }
}
