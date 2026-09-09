package alex;

import java.nio.file.Path;
import java.util.List;

import alex.exception.AlexException;
import alex.parser.CommandType;
import alex.parser.Parser;
import alex.storage.Storage;
import alex.storage.StorageException;
import alex.task.Task;
import alex.task.TaskList;
import alex.ui.Ui;

/**
 * Runs the Alex task manager.
 */
public class Alex {
    private final Storage storage;
    private final Ui ui;
    private final TaskList tasks;
    private final String loadingErrorMessage;

    /**
     * Creates Alex and loads its saved tasks.
     *
     * @param filePath path of the task data file.
     */
    public Alex(String filePath) {
        ui = new Ui();
        storage = new Storage(Path.of(filePath));

        TaskList loadedTasks;
        String loadingErrorMessage = null;
        try {
            loadedTasks = new TaskList(storage.loadTasks());
        } catch (StorageException e) {
            loadedTasks = new TaskList();
            loadingErrorMessage = e.getMessage();
        }
        tasks = loadedTasks;
        this.loadingErrorMessage = loadingErrorMessage;
    }

    /**
     * Runs the command loop until the user exits or storage becomes unavailable.
     */
    public void run() {
        ui.showWelcome();
        if (loadingErrorMessage != null) {
            ui.showLoadingError(loadingErrorMessage);
            return;
        }

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            CommandType commandType = Parser.parseCommandType(command);

            try {
                String response = executeCommand(command, commandType);
                ui.showResponse(response);

                if (commandType == CommandType.BYE) {
                    break;
                }
            } catch (AlexException e) {
                ui.showError(e.getMessage());
            } catch (StorageException e) {
                ui.showError(e.getMessage());
                break;
            }
        }
    }

    /**
     * Processes a user command and returns Alex's response.
     *
     * @param input user command.
     * @return Alex's response.
     */
    public String getResponse(String input) {
        if (loadingErrorMessage != null) {
            return "Sorry! " + loadingErrorMessage
                    + "\nPlease repair or remove the data file, then restart Alex.";
        }

        String command = input.trim();
        CommandType commandType = Parser.parseCommandType(command);

        try {
            return executeCommand(command, commandType);
        } catch (AlexException | StorageException e) {
            return "Sorry! " + e.getMessage();
        }
    }

    /**
     * Executes one parsed command and returns Alex's response.
     *
     * @param command full user command.
     * @param commandType parsed type of the command.
     * @return Alex's response.
     * @throws AlexException if the command is invalid.
     * @throws StorageException if the updated tasks cannot be saved.
     */
    private String executeCommand(String command, CommandType commandType)
            throws AlexException, StorageException {
        assert commandType == Parser.parseCommandType(command)
                : "Command type must match the command text";

        return switch (commandType) {
            case BYE -> "Bye. Hope to see you again soon!";
            case LIST -> getTaskListResponse();
            case MARK -> markTask(command);
            case UNMARK -> unmarkTask(command);
            case DELETE -> deleteTask(command);
            case FIND -> findTasks(command);
            case TODO, DEADLINE, EVENT -> addTask(command, commandType);
            case UNKNOWN -> throw new AlexException("I don't recognize that command.");
        };
    }

    private String markTask(String command) throws AlexException, StorageException {
        int index = Parser.parseTaskIndex(command, CommandType.MARK, tasks.getSize());
        Task task = tasks.get(index);
        task.markAsDone();
        saveTasks();

        return "Nice! I've marked this task as done:\n"
                + "   " + task;
    }

    private String unmarkTask(String command) throws AlexException, StorageException {
        int index = Parser.parseTaskIndex(command, CommandType.UNMARK, tasks.getSize());
        Task task = tasks.get(index);
        task.markAsUndone();
        saveTasks();

        return "OK, I've marked this task as not done yet:\n"
                + "   " + task;
    }

    private String deleteTask(String command) throws AlexException, StorageException {
        int index = Parser.parseTaskIndex(command, CommandType.DELETE, tasks.getSize());
        Task removedTask = tasks.delete(index);
        saveTasks();

        return "Noted. I've removed this task:\n"
                + "   " + removedTask + "\n"
                + "Now you have " + tasks.getSize() + " task(s) in the list.";
    }

    private String getTaskListResponse() {
        return formatTasks("Here are the tasks in your list:", tasks.getTasks());
    }

    private String findTasks(String command) throws AlexException {
        String keyword = Parser.parseFindKeyword(command);
        List<Task> matchingTasks = tasks.find(keyword);
        return formatTasks("Here are the matching tasks in your list:", matchingTasks);
    }

    private static String formatTasks(String heading, List<Task> tasksToFormat) {
        StringBuilder response = new StringBuilder(heading);

        for (int i = 0; i < tasksToFormat.size(); i++) {
            response.append("\n ")
                    .append(i + 1)
                    .append(".")
                    .append(tasksToFormat.get(i));
        }

        return response.toString();
    }

    private String addTask(String command, CommandType commandType)
            throws AlexException, StorageException {
        assert commandType == CommandType.TODO
                || commandType == CommandType.DEADLINE
                || commandType == CommandType.EVENT
                : "Only task-creation commands can add tasks";

        Task task = Parser.parseTask(command, commandType);
        tasks.add(task);
        saveTasks();

        return "Got it. I've added this task:\n"
                + "   " + task + "\n"
                + "Now you have " + tasks.getSize() + " task(s) in the list.";
    }

    private void saveTasks() throws StorageException {
        storage.saveTasks(tasks.getTasks());
    }

    /**
     * Starts Alex using the default task data file.
     *
     * @param args command-line arguments, which are ignored.
     */
    public static void main(String[] args) {
        new Alex("data/alex.txt").run();
    }
}
