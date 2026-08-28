package alex.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import alex.exception.AlexException;

public class ParserTest {
    @Test
    public void parseCommandType_findCommand_returnsFind() {
        assertEquals(CommandType.FIND, Parser.parseCommandType("find book"));
    }

    @Test
    public void parseTaskIndex_firstTask_returnsZero() throws AlexException {
        assertEquals(0, Parser.parseTaskIndex("delete 1", CommandType.DELETE, 3));
    }

    @Test
    public void parseTaskIndex_lastTask_returnsLastZeroBasedIndex() throws AlexException {
        assertEquals(2, Parser.parseTaskIndex("delete 3", CommandType.DELETE, 3));
    }

    @Test
    public void parseTaskIndex_extraWhitespace_returnsCorrectIndex() throws AlexException {
        assertEquals(1, Parser.parseTaskIndex("delete    2   ", CommandType.DELETE, 3));
    }

    @Test
    public void parseTaskIndex_missingTaskNumber_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class,
                () -> Parser.parseTaskIndex("delete", CommandType.DELETE, 3));

        assertEquals("Please provide a task number after 'delete'.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_nonIntegerTaskNumber_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class,
                () -> Parser.parseTaskIndex("delete two", CommandType.DELETE, 3));

        assertEquals("'two' is not a valid task number.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_zeroTaskNumber_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class,
                () -> Parser.parseTaskIndex("delete 0", CommandType.DELETE, 3));

        assertEquals("Please choose a task number from 1 to 3.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_taskNumberAboveTaskCount_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class,
                () -> Parser.parseTaskIndex("delete 4", CommandType.DELETE, 3));

        assertEquals("Please choose a task number from 1 to 3.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_emptyTaskList_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class,
                () -> Parser.parseTaskIndex("delete 1", CommandType.DELETE, 0));

        assertEquals("There are no tasks in the list yet.", exception.getMessage());
    }

    @Test
    public void parseFindKeyword_validKeyword_returnsKeyword() throws AlexException {
        assertEquals("book", Parser.parseFindKeyword("find    book   "));
    }

    @Test
    public void parseFindKeyword_missingKeyword_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class,
                () -> Parser.parseFindKeyword("find"));

        assertEquals("Please provide a keyword after 'find'.", exception.getMessage());
    }
}
