package alex.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import alex.exception.AlexException;
import alex.task.Task;

/**
 * Tests command parsing behavior.
 */
public class ParserTest {
    @Test
    public void parseCommandType_findCommand_returnsFind() {
        assertEquals(CommandType.FIND, Parser.parseCommandType("find book"));
    }

    @Test
    public void parseCommandType_upcomingCommand_returnsUpcoming() {
        assertEquals(CommandType.UPCOMING, Parser.parseCommandType("upcoming"));
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
        AlexException exception = assertThrows(AlexException.class, () ->
                Parser.parseTaskIndex("delete", CommandType.DELETE, 3));

        assertEquals("Please provide a task number after 'delete'.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_nonIntegerTaskNumber_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class, () ->
                Parser.parseTaskIndex("delete two", CommandType.DELETE, 3));

        assertEquals("'two' is not a valid task number.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_zeroTaskNumber_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class, () ->
                Parser.parseTaskIndex("delete 0", CommandType.DELETE, 3));

        assertEquals("Please choose a task number from 1 to 3.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_taskNumberAboveTaskCount_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class, () ->
                Parser.parseTaskIndex("delete 4", CommandType.DELETE, 3));

        assertEquals("Please choose a task number from 1 to 3.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_emptyTaskList_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class, () ->
                Parser.parseTaskIndex("delete 1", CommandType.DELETE, 0));

        assertEquals("There are no tasks in the list yet.", exception.getMessage());
    }

    @Test
    public void parseFindKeyword_validKeyword_returnsKeyword() throws AlexException {
        assertEquals("book", Parser.parseFindKeyword("find    book   "));
    }

    @Test
    public void parseFindKeyword_missingKeyword_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class, () ->
                Parser.parseFindKeyword("find"));

        assertEquals("Please provide a keyword after 'find'.", exception.getMessage());
    }

    @Test
    public void parseTask_deadlineWithTime_returnsTimedDeadline() throws AlexException {
        Task task = Parser.parseTask(
                "deadline submit report /by 2019-12-02 1800", CommandType.DEADLINE);

        assertEquals("D | 0 | submit report | 2019-12-02 1800", task.toDataString());
    }

    @Test
    public void parseTask_eventEndsBeforeStart_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class, () ->
                Parser.parseTask(
                        "event meeting /from 2019-12-02 1800 /to 2019-12-02 1700",
                        CommandType.EVENT));

        assertEquals("The event end must be after its start.", exception.getMessage());
    }

    @Test
    public void parseTask_eventEndEqualsStart_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class, () ->
                Parser.parseTask(
                        "event meeting /from 2019-12-02 1800 /to 2019-12-02 1800",
                        CommandType.EVENT));

        assertEquals("The event end must be after its start.", exception.getMessage());
    }

    @Test
    public void parseTask_eventDatesAreEqual_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class, () ->
                Parser.parseTask(
                        "event meeting /from 2019-12-02 /to 2019-12-02",
                        CommandType.EVENT));

        assertEquals("The event end must be after its start.", exception.getMessage());
    }

    @Test
    public void parseTask_eventEndsAfterStart_returnsTimedEvent() throws AlexException {
        Task task = Parser.parseTask(
                "event meeting /from 2019-12-02 1700 /to 2019-12-02 1800",
                CommandType.EVENT);

        assertEquals("E | 0 | meeting | 2019-12-02 1700 | 2019-12-02 1800",
                task.toDataString());
    }

    @Test
    public void parseTask_deadlineWithImpossibleTime_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class, () ->
                Parser.parseTask(
                        "deadline submit report /by 2019-12-02 2460", CommandType.DEADLINE));

        assertEquals("Please enter the date and optional time in yyyy-MM-dd [HHmm] format, "
                + "for example 2019-12-02 1800.", exception.getMessage());
    }

    @Test
    public void parseTask_todoDescriptionContainsFieldSeparator_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class, () ->
                Parser.parseTask("todo compare A | B", CommandType.TODO));

        assertEquals("Task descriptions cannot contain '|'.", exception.getMessage());
    }

    @Test
    public void parseTask_deadlineDescriptionContainsFieldSeparator_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class, () ->
                Parser.parseTask(
                        "deadline compare A | B /by 2019-12-02", CommandType.DEADLINE));

        assertEquals("Task descriptions cannot contain '|'.", exception.getMessage());
    }

    @Test
    public void parseTask_eventDescriptionContainsFieldSeparator_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class, () ->
                Parser.parseTask(
                        "event compare A | B /from 2019-12-02 /to 2019-12-03",
                        CommandType.EVENT));

        assertEquals("Task descriptions cannot contain '|'.", exception.getMessage());
    }
}
