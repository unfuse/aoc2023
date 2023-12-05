package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class InputUtil {

    private InputUtil() {
        throw new RuntimeException();
    }

    public static List<String> lines(final String day) {
        try {
            return Files.readAllLines(Path.of("inputs/" + day + ".txt"));
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String file(final String day) {
        try {
            return Files.readString(Path.of("inputs/" + day + ".txt"));
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Stream<String> splitStream(final String string, final String needle) {
        return Stream.of(string.split(needle)).map(String::trim);
    }

    public static Split splitHalf(final String string, final String needle) {
        final String[] split = string.split(needle);
        return new Split(split[0].trim(), split[1].trim());
    }

    public record Split(String left, String right) {
    }
}
