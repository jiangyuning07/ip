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

    public static CommandType parseCommandType(String command) {
        return CommandType.from(command);
    }

    /**
     * Extracts and validates the one-based task number in a command.
     *
     * @param command full user command
     * @param commandType command whose argument is being parsed
     * @param taskCount number of tasks currently available
     * @return corresponding zero-based task index
     * @throws AlexException if the argument is missing, invalid, or out of range
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
     * @param command full user command
     * @param commandType type of task to create
     * @return task represented by the command
     * @throws AlexException if required task details are missing or invalid
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
        int bySeparator = details.indexOf("/by");

        if (bySeparator < 0) {
            throw new AlexException("A deadline needs a description and a /by date.");
        }

        String description = details.substring(0, bySeparator).trim();
        String byText = details.substring(bySeparator + "/by".length()).trim();
        if (description.isEmpty()) {
            throw new AlexException("The deadline description cannot be empty.");
        }
        if (byText.isEmpty()) {
            throw new AlexException("The deadline date cannot be empty.");
        }

        LocalDate by = DateParser.parse(byText);
        return new Deadline(description, by);
    }

    private static Task parseEvent(String command) throws AlexException {
        String details = getArguments(command, CommandType.EVENT);
        int fromSeparator = details.indexOf("/from");

        if (fromSeparator < 0) {
            throw new AlexException("An event needs a description, a /from date, and a /to date.");
        }

        int toSeparator = details.indexOf("/to", fromSeparator + "/from".length());
        if (toSeparator < 0) {
            throw new AlexException("Please specify the event's end date using /to.");
        }

        String description = details.substring(0, fromSeparator).trim();
        String fromText = details.substring(fromSeparator + "/from".length(), toSeparator).trim();
        String toText = details.substring(toSeparator + "/to".length()).trim();
        if (description.isEmpty()) {
            throw new AlexException("The event description cannot be empty.");
        }
        if (fromText.isEmpty()) {
            throw new AlexException("The event start date cannot be empty.");
        }
        if (toText.isEmpty()) {
            throw new AlexException("The event end date cannot be empty.");
        }

        LocalDate from = DateParser.parse(fromText);
        LocalDate to = DateParser.parse(toText);
        return new Event(description, from, to);
    }

    private static String getArguments(String command, CommandType commandType) {
        return command.substring(commandType.getKeyword().length()).trim();
    }
}