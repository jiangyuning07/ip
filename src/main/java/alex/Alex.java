package alex;

import java.nio.file.Path;

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
    private final String loadingError;

    /**
     * Creates Alex and loads its saved tasks.
     *
     * @param filePath path of the task data file.
     */
    public Alex(String filePath) {
        ui = new Ui();
        storage = new Storage(Path.of(filePath));

        TaskList loadedTasks;
        String error = null;
        try {
            loadedTasks = new TaskList(storage.loadTasks());
        } catch (StorageException e) {
            loadedTasks = new TaskList();
            error = e.getMessage();
        }
        tasks = loadedTasks;
        loadingError = error;
    }

    /**
     * Runs the command loop until the user exits or storage becomes unavailable.
     */
    public void run() {
        ui.showWelcome();
        if (loadingError != null) {
            ui.showLoadingError(loadingError);
            return;
        }

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            CommandType commandType = Parser.parseCommandType(command);

            try {
                if (!executeCommand(command, commandType)) {
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
     * Executes one parsed command.
     *
     * @param command full user command.
     * @param commandType parsed type of the command.
     * @return false when Alex should stop accepting commands.
     */
    private boolean executeCommand(String command, CommandType commandType)
            throws AlexException, StorageException {
        switch (commandType) {
            case BYE:
                ui.showFarewell();
                return false;
            case LIST:
                ui.showTaskList(tasks);
                break;
            case MARK:
                markTask(command);
                break;
            case UNMARK:
                unmarkTask(command);
                break;
            case DELETE:
                deleteTask(command);
                break;
            case TODO, DEADLINE, EVENT:
                addTask(command, commandType);
                break;
            default:
                throw new AlexException("I don't recognize that command.");
        }
        return true;
    }

    private void markTask(String command) throws AlexException, StorageException {
        int index = Parser.parseTaskIndex(command, CommandType.MARK, tasks.getSize());
        Task task = tasks.get(index);
        task.markAsDone();
        saveTasks();
        ui.showTaskMarked(task);
    }

    private void unmarkTask(String command) throws AlexException, StorageException {
        int index = Parser.parseTaskIndex(command, CommandType.UNMARK, tasks.getSize());
        Task task = tasks.get(index);
        task.markAsUndone();
        saveTasks();
        ui.showTaskUnmarked(task);
    }

    private void deleteTask(String command) throws AlexException, StorageException {
        int index = Parser.parseTaskIndex(command, CommandType.DELETE, tasks.getSize());
        Task removedTask = tasks.delete(index);
        saveTasks();
        ui.showTaskDeleted(removedTask, tasks.getSize());
    }

    private void addTask(String command, CommandType commandType)
            throws AlexException, StorageException {
        Task task = Parser.parseTask(command, commandType);
        tasks.add(task);
        saveTasks();
        ui.showTaskAdded(task, tasks.getSize());
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
