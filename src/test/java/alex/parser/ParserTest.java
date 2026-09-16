package alex.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

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
    public void parseCommandType_listCommandWithDetails_returnsList() {
        assertEquals(CommandType.LIST, Parser.parseCommandType("list extra details"));
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
    public void parseTask_missingRequiredDetails_throwsSpecificExceptions() {
        List<InvalidTaskCommand> invalidCommands = List.of(
                new InvalidTaskCommand(
                        "todo", CommandType.TODO, "A todo needs a description."),
                new InvalidTaskCommand(
                        "deadline /by 2019-12-02", CommandType.DEADLINE,
                        "The deadline description cannot be empty."),
                new InvalidTaskCommand(
                        "deadline submit report /by", CommandType.DEADLINE,
                        "The deadline date cannot be empty."),
                new InvalidTaskCommand(
                        "event /from 2019-12-02 /to 2019-12-03", CommandType.EVENT,
                        "The event description cannot be empty."),
                new InvalidTaskCommand(
                        "event meeting /from /to 2019-12-03", CommandType.EVENT,
                        "The event start date cannot be empty."),
                new InvalidTaskCommand(
                        "event meeting /from 2019-12-02 /to", CommandType.EVENT,
                        "The event end date cannot be empty."));

        for (InvalidTaskCommand invalidCommand : invalidCommands) {
            assertTaskParsingFails(
                    invalidCommand.command(), invalidCommand.commandType(), invalidCommand.message());
        }
    }

    @Test
    public void parseTask_deadlineWithTime_returnsTimedDeadline() throws AlexException {
        Task task = Parser.parseTask(
                "deadline submit report /by 2019-12-02 1800", CommandType.DEADLINE);

        assertEquals("D | 0 | submit report | 2019-12-02 1800", task.toDataString());
    }

    @Test
    public void parseTask_deadlineWithRepeatedDateDelimiter_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class, () ->
                Parser.parseTask(
                        "deadline report /by 2019-12-02 /by 2019-12-03",
                        CommandType.DEADLINE));

        assertEquals("A deadline must contain exactly one standalone /by delimiter.",
                exception.getMessage());
    }

    @Test
    public void parseTask_deadlineWithDelimiterLikeDescription_returnsDeadline()
            throws AlexException {
        Task task = Parser.parseTask(
                "deadline review /bypass logic /by 2019-12-02", CommandType.DEADLINE);

        assertEquals("D | 0 | review /bypass logic | 2019-12-02", task.toDataString());
    }

    @Test
    public void parseTask_deadlineWithoutStandaloneDateDelimiter_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class, () ->
                Parser.parseTask(
                        "deadline review /bypass logic 2019-12-02", CommandType.DEADLINE));

        assertEquals("A deadline must contain exactly one standalone /by delimiter.",
                exception.getMessage());
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
    public void parseTask_eventWithRepeatedStartDelimiter_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class, () ->
                Parser.parseTask(
                        "event meeting /from 2019-12-02 /from 2019-12-03 /to 2019-12-04",
                        CommandType.EVENT));

        assertEquals("An event must contain exactly one standalone /from delimiter.",
                exception.getMessage());
    }

    @Test
    public void parseTask_eventWithoutStartDelimiter_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class, () ->
                Parser.parseTask(
                        "event meeting /to 2019-12-03", CommandType.EVENT));

        assertEquals("An event must contain exactly one standalone /from delimiter.",
                exception.getMessage());
    }

    @Test
    public void parseTask_eventWithRepeatedEndDelimiter_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class, () ->
                Parser.parseTask(
                        "event meeting /from 2019-12-02 /to 2019-12-03 /to 2019-12-04",
                        CommandType.EVENT));

        assertEquals("An event must contain exactly one standalone /to delimiter.",
                exception.getMessage());
    }

    @Test
    public void parseTask_eventWithoutEndDelimiter_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class, () ->
                Parser.parseTask(
                        "event meeting /from 2019-12-02", CommandType.EVENT));

        assertEquals("An event must contain exactly one standalone /to delimiter.",
                exception.getMessage());
    }

    @Test
    public void parseTask_eventWithMisorderedDelimiters_exceptionThrown() {
        AlexException exception = assertThrows(AlexException.class, () ->
                Parser.parseTask(
                        "event meeting /to 2019-12-03 /from 2019-12-02",
                        CommandType.EVENT));

        assertEquals("An event's /from delimiter must appear before its /to delimiter.",
                exception.getMessage());
    }

    @Test
    public void parseTask_eventWithDelimiterLikeDescription_returnsEvent()
            throws AlexException {
        Task task = Parser.parseTask(
                "event discuss /fromage and /today /from 2019-12-02 /to 2019-12-03",
                CommandType.EVENT);

        assertEquals("E | 0 | discuss /fromage and /today | 2019-12-02 | 2019-12-03",
                task.toDataString());
    }

    @Test
    public void parseTask_deadlineWithInvalidDateTimes_exceptionThrown() {
        String expectedMessage = "Please enter the date and optional time in yyyy-MM-dd [HHmm] "
                + "format, for example 2019-12-02 1800.";

        List<String> invalidCommands = List.of(
                "deadline submit report /by 2019-12-02 2460",
                "deadline submit report /by 2019-12-02 1800 extra");

        for (String invalidCommand : invalidCommands) {
            assertTaskParsingFails(invalidCommand, CommandType.DEADLINE, expectedMessage);
        }
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

    private static void assertTaskParsingFails(
            String command, CommandType commandType, String expectedMessage) {
        AlexException exception = assertThrows(AlexException.class, () ->
                Parser.parseTask(command, commandType));

        assertEquals(expectedMessage, exception.getMessage(), command);
    }

    private record InvalidTaskCommand(
            String command, CommandType commandType, String message) {
    }
}
