import util.InputUtil;

import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day1 {

    private static final String TEST = """
            two1nine
            eightwothree
            abcone2threexyz
            xtwone3four
            4nineeightseven2
            zoneight234
            7pqrstsixteen""";

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

    public static void main(final String[] args) {
        part2();
    }

    public static void part1() {
        final List<String> input = InputUtil.lines("day01");

        final var result = input.stream()
                .map(Day1::getConcatFirstLastNumber)
                .mapToInt(Integer::valueOf)
                .sum();

        System.out.println(result);
    }

    public static void part2() {
        final List<String> input = InputUtil.lines("day01");

        final var result = input.stream()
                .map(Day1::getConcatFirstLastNumber2)
                .mapToInt(Integer::valueOf)
                .sum();

        System.out.println(result);
    }

    private static String getConcatFirstLastNumber(final String str) {
        final List<String> nums = Pattern.compile("\\d")
                .matcher(str)
                .results()
                .map(MatchResult::group)
                .toList();
        return nums.get(0) + nums.get(nums.size()-1);
    }

    private static String getConcatFirstLastNumber2(final String str) {
        final String mapKeys = LOOKUP.keySet().stream().reduce("", (acc, s) -> acc + "|" + s);
        final List<String> nums = Pattern.compile("\\d" + mapKeys)
                .matcher(str)
                .results()
                .map(MatchResult::group)
                .toList();
        final String first = LOOKUP.getOrDefault(nums.get(0), nums.get(0));
        final String last = LOOKUP.getOrDefault(nums.get(nums.size()-1), nums.get(nums.size()-1));
        return first + last;
    }
}
