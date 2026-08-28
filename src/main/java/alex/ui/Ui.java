package alex.ui;

import java.util.Scanner;

import alex.task.Task;
import alex.task.TaskList;

/**
 * Handles console input and output for Alex.
 */
public class Ui {
    private static final String DIVIDER =
            "____________________________________________________________";
    private static final String BANNER = "    _    _           \n"
            + "   / \\  | | _____  __\n"
            + "  / _ \\ | |/ _ \\ \\/ /\n"
            + " / ___ \\| |  __/>  < \n"
            + "/_/   \\_\\_|\\___/_/\\_\\\n";

    private final Scanner scanner;

    /**
     * Creates a console UI that reads from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays Alex's welcome message.
     */
    public void showWelcome() {
        System.out.println(DIVIDER);
        System.out.print(BANNER);
        System.out.println("Hello! I'm Alex.");
        System.out.println("What can I do for you?");
        System.out.println(DIVIDER);
    }

    /**
     * Checks whether another command is available.
     *
     * @return whether another command can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next user command.
     *
     * @return the next command.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays Alex's farewell message.
     */
    public void showFarewell() {
        showMessage("Bye. Hope to see you again soon!");
    }

    /**
     * Displays all tasks in their current order.
     *
     * @param tasks tasks to display.
     */
    public void showTaskList(TaskList tasks) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
        System.out.println(DIVIDER);
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task added task.
     * @param taskCount updated task count.
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println("Now you have " + taskCount + " task(s) in the list.");
        System.out.println(DIVIDER);
    }

    /**
     * Displays confirmation that a task was deleted.
     *
     * @param task deleted task.
     * @param taskCount updated task count.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("   " + task);
        System.out.println("Now you have " + taskCount + " task(s) in the list.");
        System.out.println(DIVIDER);
    }

    /**
     * Displays confirmation that a task was marked complete.
     *
     * @param task updated task.
     */
    public void showTaskMarked(Task task) {
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("   " + task);
        System.out.println(DIVIDER);
    }

    /**
     * Displays confirmation that a task was marked incomplete.
     *
     * @param task updated task.
     */
    public void showTaskUnmarked(Task task) {
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("   " + task);
        System.out.println(DIVIDER);
    }

    /**
     * Displays a user-facing error message.
     *
     * @param message explanation of the error.
     */
    public void showError(String message) {
        showMessage("Sorry! " + message);
    }

    /**
     * Displays an error that prevented saved tasks from loading.
     *
     * @param message explanation of the loading error.
     */
    public void showLoadingError(String message) {
        System.out.println("Sorry! " + message);
        System.out.println("Please repair or remove the data file, then restart Alex.");
        System.out.println(DIVIDER);
    }

    private void showMessage(String message) {
        System.out.println(message);
        System.out.println(DIVIDER);
    }
}