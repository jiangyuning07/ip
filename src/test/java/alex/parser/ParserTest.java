package alex.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import alex.exception.AlexException;

/**
 * Tests task index parsing behavior.
 */
public class ParserTest {
    /**
     * Verifies that the first task number maps to index zero.
     *
     * @throws AlexException if the valid command cannot be parsed.
     */
    @Test
    public void parseTaskIndex_firstTask_returnsZero() throws AlexException {
        assertEquals(0, Parser.parseTaskIndex("delete 1", CommandType.DELETE, 3));
    }

    /**
     * Verifies that the last task number maps to the last index.
     *
     * @throws AlexException if the valid command cannot be parsed.
     */
    @Test
    public void parseTaskIndex_lastTask_returnsLastZeroBasedIndex() throws AlexException {
        assertEquals(2, Parser.parseTaskIndex("delete 3", CommandType.DELETE, 3));
    }

    /**
     * Verifies that extra whitespace around a task number is accepted.
     *
     * @throws AlexException if the valid command cannot be parsed.
     */
    @Test
    public void parseTaskIndex_extraWhitespace_returnsCorrectIndex() throws AlexException {
        assertEquals(1, Parser.parseTaskIndex("delete    2   ", CommandType.DELETE, 3));
    }

    /**
     * Verifies that a missing task number is rejected.
     */
    @Test
    public void parseTaskIndex_missingTaskNumber_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class,
                () -> Parser.parseTaskIndex("delete", CommandType.DELETE, 3));

        assertEquals("Please provide a task number after 'delete'.", exception.getMessage());
    }

    /**
     * Verifies that a non-integer task number is rejected.
     */
    @Test
    public void parseTaskIndex_nonIntegerTaskNumber_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class,
                () -> Parser.parseTaskIndex("delete two", CommandType.DELETE, 3));

        assertEquals("'two' is not a valid task number.", exception.getMessage());
    }

    /**
     * Verifies that task number zero is rejected.
     */
    @Test
    public void parseTaskIndex_zeroTaskNumber_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class,
                () -> Parser.parseTaskIndex("delete 0", CommandType.DELETE, 3));

        assertEquals("Please choose a task number from 1 to 3.", exception.getMessage());
    }

    /**
     * Verifies that a task number above the task count is rejected.
     */
    @Test
    public void parseTaskIndex_taskNumberAboveTaskCount_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class,
                () -> Parser.parseTaskIndex("delete 4", CommandType.DELETE, 3));

        assertEquals("Please choose a task number from 1 to 3.", exception.getMessage());
    }

    /**
     * Verifies that task selection from an empty list is rejected.
     */
    @Test
    public void parseTaskIndex_emptyTaskList_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class,
                () -> Parser.parseTaskIndex("delete 1", CommandType.DELETE, 0));

        assertEquals("There are no tasks in the list yet.", exception.getMessage());
    }
}
