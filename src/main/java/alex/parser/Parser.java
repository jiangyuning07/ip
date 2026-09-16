package alex.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import alex.exception.AlexException;
import alex.task.Deadline;
import alex.task.Event;
import alex.task.Task;
import alex.task.Todo;
import alex.util.DateParser;
import alex.util.TaskDateTime;

/**
 * Interprets user commands and converts their arguments into domain objects.
 */
public class Parser {
    private static final String DEADLINE_DATE_MARKER = "/by";
    private static final String EVENT_START_DATE_MARKER = "/from";
    private static final String EVENT_END_DATE_MARKER = "/to";
    private static final Pattern DEADLINE_DATE_MARKER_PATTERN = Pattern.compile(
            "(?<!\\S)" + Pattern.quote(DEADLINE_DATE_MARKER) + "(?!\\S)");
    private static final Pattern EVENT_START_DATE_MARKER_PATTERN = Pattern.compile(
            "(?<!\\S)" + Pattern.quote(EVENT_START_DATE_MARKER) + "(?!\\S)");
    private static final Pattern EVENT_END_DATE_MARKER_PATTERN = Pattern.compile(
            "(?<!\\S)" + Pattern.quote(EVENT_END_DATE_MARKER) + "(?!\\S)");

    /**
     * Identifies the type of a user command.
     *
     * @param command full user command.
     * @return the matching command type.
     */
    public static CommandType parseCommandType(String command) {
        return CommandType.parse(command);
    }

    /**
     * Ensures that a command which takes no arguments has no additional details.
     *
     * @param command full user command.
     * @param commandType command whose arguments are being validated.
     * @throws AlexException if additional details follow the command keyword.
     */
    public static void validateNoArguments(String command, CommandType commandType)
            throws AlexException {
        assert commandType != CommandType.UNKNOWN && !commandType.canAcceptArguments()
                : "Command type must be a known command that rejects arguments";
        assert CommandType.parse(command) == commandType
                : "Command type must match the command text";

        if (!getArguments(command, commandType).isEmpty()) {
            throw new AlexException("The '" + commandType.getKeyword()
                    + "' command does not accept additional details.");
        }
    }

    /**
     * Extracts and validates the one-based task number in a command.
     *
     * @param command full user command.
     * @param commandType command whose argument is being parsed.
     * @param taskCount number of tasks currently available.
     * @return corresponding zero-based task index.
     * @throws AlexException if the argument is missing, invalid, or out of range.
     */
    public static int parseTaskIndex(String command, CommandType commandType, int taskCount)
            throws AlexException {
        assert taskCount >= 0 : "Task count cannot be negative";
        assert commandType == CommandType.MARK
                || commandType == CommandType.UNMARK
                || commandType == CommandType.DELETE
                : "Only task-selection commands have a task index";
        assert CommandType.parse(command) == commandType
                : "Command type must match the command text";

        String commandName = commandType.getKeyword();
        String taskNumberText = command.substring(commandName.length()).trim();

        if (taskNumberText.isEmpty()) {
            throw new AlexException("Please provide a task number after '" + commandName + "'.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            throw new AlexException("'" + taskNumberText + "' is not a valid task number.");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            if (taskCount == 0) {
                throw new AlexException("There are no tasks in the list yet.");
            }
            throw new AlexException("Please choose a task number from 1 to " + taskCount + ".");
        }

        int taskIndex = taskNumber - 1;
        assert taskIndex >= 0 && taskIndex < taskCount
                : "Parsed task index must be within the task list";
        return taskIndex;
    }

    /**
     * Extracts and validates the keyword in a find command.
     *
     * @param command full user command.
     * @return keyword to search for.
     * @throws AlexException if the keyword is missing.
     */
    public static String parseFindKeyword(String command) throws AlexException {
        String keyword = getArguments(command, CommandType.FIND);
        if (keyword.isEmpty()) {
            throw new AlexException("Please provide a keyword after 'find'.");
        }
        return keyword;
    }

    /**
     * Creates a task from a todo, deadline, or event command.
     *
     * @param command full user command.
     * @param commandType type of task to create.
     * @return task represented by the command.
     * @throws AlexException if required task details are missing or invalid.
     */
    public static Task parseTask(String command, CommandType commandType) throws AlexException {
        assert commandType == CommandType.TODO
                || commandType == CommandType.DEADLINE
                || commandType == CommandType.EVENT
                : "Only task-creation commands can be parsed as tasks";

        return switch (commandType) {
            case TODO -> parseTodo(command);
            case DEADLINE -> parseDeadline(command);
            case EVENT -> parseEvent(command);
            default -> throw new AlexException("This command does not create a task.");
        };
    }

    private static Task parseTodo(String command) throws AlexException {
        String description = getArguments(command, CommandType.TODO);
        if (description.isEmpty()) {
            throw new AlexException("A todo needs a description.");
        }
        validateDescriptionCharacters(description);
        return new Todo(description);
    }

    private static Task parseDeadline(String command) throws AlexException {
        String details = getArguments(command, CommandType.DEADLINE);
        List<Integer> dueDateSeparators = findMarkerPositions(
                details, DEADLINE_DATE_MARKER_PATTERN);

        if (dueDateSeparators.size() != 1) {
            throw new AlexException("A deadline must contain exactly one standalone "
                    + DEADLINE_DATE_MARKER + " delimiter.");
        }

        int dueDateSeparator = dueDateSeparators.get(0);
        String description = details.substring(0, dueDateSeparator).trim();
        String dueDateText = details.substring(
                dueDateSeparator + DEADLINE_DATE_MARKER.length()).trim();
        if (description.isEmpty()) {
            throw new AlexException("The deadline description cannot be empty.");
        }
        validateDescriptionCharacters(description);
        if (dueDateText.isEmpty()) {
            throw new AlexException("The deadline date cannot be empty.");
        }

        TaskDateTime dueDateTime = DateParser.parseTaskDateTime(dueDateText);
        return new Deadline(description, dueDateTime);
    }

    private static Task parseEvent(String command) throws AlexException {
        String details = getArguments(command, CommandType.EVENT);
        List<Integer> startDateSeparators = findMarkerPositions(
                details, EVENT_START_DATE_MARKER_PATTERN);
        List<Integer> endDateSeparators = findMarkerPositions(
                details, EVENT_END_DATE_MARKER_PATTERN);

        if (startDateSeparators.size() != 1) {
            throw new AlexException("An event must contain exactly one standalone "
                    + EVENT_START_DATE_MARKER + " delimiter.");
        }
        if (endDateSeparators.size() != 1) {
            throw new AlexException("An event must contain exactly one standalone "
                    + EVENT_END_DATE_MARKER + " delimiter.");
        }

        int startDateSeparator = startDateSeparators.get(0);
        int endDateSeparator = endDateSeparators.get(0);
        if (startDateSeparator >= endDateSeparator) {
            throw new AlexException("An event's " + EVENT_START_DATE_MARKER
                    + " delimiter must appear before its " + EVENT_END_DATE_MARKER + " delimiter.");
        }

        String description = details.substring(0, startDateSeparator).trim();
        String startDateText = details.substring(
                startDateSeparator + EVENT_START_DATE_MARKER.length(), endDateSeparator).trim();
        String endDateText = details.substring(
                endDateSeparator + EVENT_END_DATE_MARKER.length()).trim();
        if (description.isEmpty()) {
            throw new AlexException("The event description cannot be empty.");
        }
        validateDescriptionCharacters(description);
        if (startDateText.isEmpty()) {
            throw new AlexException("The event start date cannot be empty.");
        }
        if (endDateText.isEmpty()) {
            throw new AlexException("The event end date cannot be empty.");
        }

        TaskDateTime startDateTime = DateParser.parseTaskDateTime(startDateText);
        TaskDateTime endDateTime = DateParser.parseTaskDateTime(endDateText);
        try {
            return new Event(description, startDateTime, endDateTime);
        } catch (IllegalArgumentException e) {
            throw new AlexException(e.getMessage());
        }
    }

    private static void validateDescriptionCharacters(String description) throws AlexException {
        if (description.contains("|")) {
            throw new AlexException("Task descriptions cannot contain '|'.");
        }
    }

    /**
     * Returns the starting positions of standalone markers in the supplied details.
     * A marker is standalone when whitespace or a string boundary appears on each side.
     *
     * @param details command details to search.
     * @param markerPattern pattern representing a standalone marker.
     * @return marker positions in their original order.
     */
    private static List<Integer> findMarkerPositions(String details, Pattern markerPattern) {
        ArrayList<Integer> markerPositions = new ArrayList<>();
        Matcher matcher = markerPattern.matcher(details);
        while (matcher.find()) {
            markerPositions.add(matcher.start());
        }
        return markerPositions;
    }

    private static String getArguments(String command, CommandType commandType) {
        assert CommandType.parse(command) == commandType
                : "Command type must match the command text";
        return command.substring(commandType.getKeyword().length()).trim();
    }
}
