import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;

public class mediaUtils {

    // We convert the string into LocalDateTime
    private static LocalDateTime parseDate(String dateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
            return LocalDateTime.parse(dateStr, formatter);
        } catch (Exception e) {
            return LocalDateTime.MIN; // fallback in case of a fail
        }
    }

    // We fix the array's order from oldest to newest
    public static void sortByDate(mediaFile[] media) {
        Arrays.sort(media, Comparator.comparing(m -> parseDate(m.date)));
    }

    // We get the oldest file provided
    public static mediaFile getOldest(mediaFile[] media) {
        return media[0];
    }

    // We get the newest file provided
    public static mediaFile getNewest(mediaFile[] media) {
        return media[media.length - 1];
    }
}