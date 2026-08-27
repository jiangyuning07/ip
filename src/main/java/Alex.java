import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDate;

public class Alex {
    private static final Storage STORAGE = new Storage(Path.of("data", "alex.txt"));

    private static int parseTaskIndex(String command, CommandType commandType, int taskCount) throws AlexException {
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

    public static void main(String[] args) {
        String divider = "____________________________________________________________";
        String banner = "    _    _           \n"
                + "   / \\  | | _____  __\n"
                + "  / _ \\ | |/ _ \\ \\/ /\n"
                + " / ___ \\| |  __/>  < \n"
                + "/_/   \\_\\_|\\___/_/\\_\\\n";
        String greeting = "Hello! I'm Alex.\n"
                + "What can I do for you?";
        String farewell = "Bye. Hope to see you again soon!";

        System.out.println(divider);
        System.out.print(banner);
        System.out.println(greeting);
        System.out.println(divider);

        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> tasks;
        try {
            tasks = STORAGE.loadTasks();
        } catch (StorageException e) {
            System.out.println("Sorry! " + e.getMessage());
            System.out.println("Please repair or remove the data file, then restart Alex.");
            System.out.println(divider);
            return;
        }

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            CommandType commandType = CommandType.from(command);
            System.out.println(divider);

            try {
                if (commandType == CommandType.BYE) {
                    System.out.println(farewell);
                    System.out.println(divider);
                    break;
                } else if (commandType == CommandType.LIST) {
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println(" " + (i + 1) + "." + tasks.get(i));
                    }
                    System.out.println(divider);
                } else if (commandType == CommandType.MARK) {
                    int taskIndex = parseTaskIndex(command, CommandType.MARK, tasks.size());
                    Task task = tasks.get(taskIndex);
                    task.markAsDone();
                    STORAGE.saveTasks(tasks);

                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("   " + task);
                    System.out.println(divider);
                } else if (commandType == CommandType.UNMARK) {
                    int taskIndex = parseTaskIndex(command, CommandType.UNMARK, tasks.size());
                    Task task = tasks.get(taskIndex);
                    task.markAsUndone();
                    STORAGE.saveTasks(tasks);

                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("   " + task);
                    System.out.println(divider);
                } else if (commandType == CommandType.DELETE) {
                    int taskIndex = parseTaskIndex(command, CommandType.DELETE, tasks.size());
                    Task removedTask = tasks.remove(taskIndex);
                    STORAGE.saveTasks(tasks);

                    System.out.println("Noted. I've removed this task:");
                    System.out.println("   " + removedTask);
                    System.out.println("Now you have " + tasks.size() + " task(s) in the list.");
                    System.out.println(divider);
                } else if (commandType == CommandType.TODO) {
                    String description = command.substring(CommandType.TODO.getKeyword().length()).trim();

                    if (description.isEmpty()) {
                        throw new AlexException("A todo needs a description.");
                    }

                    Task task = new Todo(description);
                    tasks.add(task);
                    STORAGE.saveTasks(tasks);

                    System.out.println("Got it. I've added this task:");
                    System.out.println("   " + task);
                    System.out.println("Now you have " + tasks.size() + " task(s) in the list.");
                    System.out.println(divider);
                } else if (commandType == CommandType.DEADLINE) {
                    String details = command.substring(CommandType.DEADLINE.getKeyword().length()).trim();
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
                    Task task = new Deadline(description, by);
                    tasks.add(task);
                    STORAGE.saveTasks(tasks);

                    System.out.println("Got it. I've added this task:");
                    System.out.println("   " + task);
                    System.out.println("Now you have " + tasks.size() + " task(s) in the list.");
                    System.out.println(divider);
                } else if (commandType == CommandType.EVENT) {
                    String details = command.substring(CommandType.EVENT.getKeyword().length()).trim();
                    int fromSeparator = details.indexOf("/from");

                    if (fromSeparator < 0) {
                        throw new AlexException("An event needs a description, a /from date, and a /to date.");
                    }

                    int toSeparator = details.indexOf("/to", fromSeparator + "/from".length());

                    if (toSeparator < 0) {
                        throw new AlexException("Please specify the event's end date using /to.");
                    }

                    String description = details.substring(0, fromSeparator).trim();
                    String fromText = details
                            .substring(fromSeparator + "/from".length(), toSeparator).trim();
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
                    Task task = new Event(description, from, to);
                    tasks.add(task);
                    STORAGE.saveTasks(tasks);

                    System.out.println("Got it. I've added this task:");
                    System.out.println("   " + task);
                    System.out.println("Now you have " + tasks.size() + " task(s) in the list.");
                    System.out.println(divider);
                } else {
                    throw new AlexException("I don't recognize that command.");
                }
            } catch (AlexException e) {
                System.out.println("Sorry! " + e.getMessage());
                System.out.println(divider);
            } catch (StorageException e) {
                System.out.println("Sorry! " + e.getMessage());
                System.out.println(divider);
                break;
            }
        }
    }
}
