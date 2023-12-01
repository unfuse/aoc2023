package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class InputUtil {

    private InputUtil() {
        throw new RuntimeException();
    }

    public static List<String> lines(final String day) {
        try {
            return Files.readAllLines(Path.of("inputs/"+day+".txt"));
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
