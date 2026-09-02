package alex.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import alex.exception.AlexException;

/**
 * Tests parsing of ISO-formatted dates.
 */
public class DateParserTest {

    /**
     * Verifies that a valid date is parsed.
     *
     * @throws AlexException if the valid date cannot be parsed.
     */
    @Test
    public void parse_validDate_returnsLocalDate() throws AlexException {
        LocalDate expectedDate = LocalDate.of(2026, 8, 28);

        LocalDate actualDate = DateParser.parse("2026-08-28");

        assertEquals(expectedDate, actualDate);
    }

    /**
     * Verifies that a valid leap day is parsed.
     *
     * @throws AlexException if the valid date cannot be parsed.
     */
    @Test
    public void parse_validLeapDay_returnsLocalDate() throws AlexException {
        LocalDate expectedDate = LocalDate.of(2024, 2, 29);

        LocalDate actualDate = DateParser.parse("2024-02-29");

        assertEquals(expectedDate, actualDate);
    }

    /**
     * Verifies that a non-ISO date format is rejected.
     */
    @Test
    public void parse_invalidFormat_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class, () ->
                DateParser.parse("28-08-2026"));

        assertEquals("Please enter the date in yyyy-MM-dd format, "
                + "for example 2019-12-02.",
                exception.getMessage());
    }

    /**
     * Verifies that an impossible calendar date is rejected.
     */
    @Test
    public void parse_impossibleDate_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class, () ->
                DateParser.parse("2026-02-30"));

        assertEquals("Please enter the date in yyyy-MM-dd format, "
                + "for example 2019-12-02.",
                exception.getMessage());
    }

    /**
     * Verifies that February 29 in a non-leap year is rejected.
     */
    @Test
    public void parse_nonLeapYearFebruary29_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class, () ->
                DateParser.parse("2025-02-29"));

        assertEquals("Please enter the date in yyyy-MM-dd format, "
                + "for example 2019-12-02.",
                exception.getMessage());
    }

    /**
     * Verifies that an empty date is rejected.
     */
    @Test
    public void parse_emptyInput_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class, () ->
                DateParser.parse(""));

        assertEquals("Please enter the date in yyyy-MM-dd format, "
                + "for example 2019-12-02.",
                exception.getMessage());
    }
}
