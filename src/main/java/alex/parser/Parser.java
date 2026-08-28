package alex.parser;

import java.time.LocalDate;

import alex.exception.AlexException;
import alex.task.Deadline;
import alex.task.Event;
import alex.task.Task;
import alex.task.Todo;
import alex.util.DateParser;

/**
 * Interprets user commands and converts their arguments into domain objects.
 */
public class Parser {

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

        return taskNumber - 1;
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
        return new Todo(description);
    }

    private static Task parseDeadline(String command) throws AlexException {
        String details = getArguments(command, CommandType.DEADLINE);
        int dueDateSeparator = details.indexOf("/by");

        if (dueDateSeparator < 0) {
            throw new AlexException("A deadline needs a description and a /by date.");
        }

        String description = details.substring(0, dueDateSeparator).trim();
        String dueDateText = details.substring(dueDateSeparator + "/by".length()).trim();
        if (description.isEmpty()) {
            throw new AlexException("The deadline description cannot be empty.");
        }
        if (dueDateText.isEmpty()) {
            throw new AlexException("The deadline date cannot be empty.");
        }

        LocalDate dueDate = DateParser.parse(dueDateText);
        return new Deadline(description, dueDate);
    }

    private static Task parseEvent(String command) throws AlexException {
        String details = getArguments(command, CommandType.EVENT);
        int startDateSeparator = details.indexOf("/from");

        if (startDateSeparator < 0) {
            throw new AlexException("An event needs a description, a /from date, and a /to date.");
        }

        int endDateSeparator = details.indexOf("/to", startDateSeparator + "/from".length());
        if (endDateSeparator < 0) {
            throw new AlexException("Please specify the event's end date using /to.");
        }

        String description = details.substring(0, startDateSeparator).trim();
        String startDateText = details.substring(
                startDateSeparator + "/from".length(), endDateSeparator).trim();
        String endDateText = details.substring(endDateSeparator + "/to".length()).trim();
        if (description.isEmpty()) {
            throw new AlexException("The event description cannot be empty.");
        }
        if (startDateText.isEmpty()) {
            throw new AlexException("The event start date cannot be empty.");
        }
        if (endDateText.isEmpty()) {
            throw new AlexException("The event end date cannot be empty.");
        }

        LocalDate startDate = DateParser.parse(startDateText);
        LocalDate endDate = DateParser.parse(endDateText);
        return new Event(description, startDate, endDate);
    }

    private static String getArguments(String command, CommandType commandType) {
        return command.substring(commandType.getKeyword().length()).trim();
    }
}
