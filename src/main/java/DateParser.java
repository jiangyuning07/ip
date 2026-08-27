import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Parses dates entered by users and formats dates for display.
 */
public class DateParser {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    /**
     * Parses a date in the ISO yyyy-MM-dd format.
     *
     * @param input date text entered by the user
     * @return the parsed date
     * @throws AlexException if the input is not a valid ISO date
     */
    public static LocalDate parse(String input) throws AlexException {
        try {
            return LocalDate.parse(input);
        } catch (DateTimeParseException e) {
            throw new AlexException("Please enter the date in yyyy-MM-dd format, "
                    + "for example 2019-12-02.");
        }
    }

    /**
     * Formats a date in the form Jan 1 2026.
     *
     * @param date date to format
     * @return the formatted date
     */
    public static String format(LocalDate date) {
        return date.format(DISPLAY_FORMAT);
    }
}
