import util.InputUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class Day1 {

    private static final Map<String, String> LOOKUP = Map.of(
            "one", "1",
            "two", "2",
            "three", "3",
            "four", "4",
            "five", "5",
            "six", "6",
            "seven", "7",
            "eight", "8",
            "nine", "9");

    private static final UnaryOperator<String> GOOD_LOOKUP = str -> LOOKUP.getOrDefault(str, str);

    private static final  String MAP_KEYS = LOOKUP
            .keySet()
            .stream()
            .reduce("", (acc, s) -> acc + "|" + s);
    private static final Pattern PATTERN_DAY_1 = Pattern.compile("\\d");
    private static final Pattern PATTERN_DAY_2 = Pattern.compile("\\d" + MAP_KEYS);

    private Day1() {
    }

    public static void main(final String[] args) {
        final List<String> input = InputUtil.lines("day01");
        doPart(input, Day1::easierConcatFirstLastNumber);
        doPart(input, Day1::harderConcatFirstLastNumber);
    }

    static void doPart(final List<String> input, final UnaryOperator<String> parser) {
        final var result = input.stream()
                .map(parser)
                .mapToInt(Integer::valueOf)
                .sum();

        System.out.println(result);
    }

    private static String easierConcatFirstLastNumber(final String str) {
        final List<String> nums = PATTERN_DAY_1
                .matcher(str)
                .results()
                .map(MatchResult::group)
                .toList();
        return nums.get(0) + nums.get(nums.size()-1);
    }

    private static String harderConcatFirstLastNumber(final String str) {
        final int strLen = str.length();
        final List<String> nums = new ArrayList<>();
        for (int i = 0; i < strLen; i++) {
            final String substr = str.substring(i);
            PATTERN_DAY_2.matcher(substr)
                    .results()
                    .map(MatchResult::group)
                    .findFirst()
                    .ifPresent(nums::add);
        }
        final String first = GOOD_LOOKUP.apply(nums.get(0));
        final String last = GOOD_LOOKUP.apply(nums.get(nums.size()-1));
        return first + last;
    }
}
