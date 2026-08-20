import java.util.Scanner;
import java.util.ArrayList;

public class Alex {
    private static int parseTaskIndex(String command, String commandName, int taskCount) throws AlexException {
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
        ArrayList<Task> tasks = new ArrayList<>();

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            System.out.println(divider);

            try {
                if (command.equals("bye")) {
                    System.out.println(farewell);
                    System.out.println(divider);
                    break;
                } else if (command.equals("list")) {
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println(" " + (i + 1) + "." + tasks.get(i));
                    }
                    System.out.println(divider);
                } else if (command.equals("mark") || command.startsWith("mark ")) {
                    int taskIndex = parseTaskIndex(command, "mark", tasks.size());
                    Task task = tasks.get(taskIndex);
                    task.markAsDone();

                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("   " + task);
                    System.out.println(divider);
                } else if (command.equals("unmark") || command.startsWith("unmark ")) {
                    int taskIndex = parseTaskIndex(command, "unmark", tasks.size());
                    Task task = tasks.get(taskIndex);
                    task.markAsUndone();

                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("   " + task);
                    System.out.println(divider);
                } else if (command.equals("delete") || command.startsWith("delete ")) {
                    int taskIndex = parseTaskIndex(command, "delete", tasks.size());
                    Task removedTask = tasks.remove(taskIndex);

                    System.out.println("Noted. I've removed this task:");
                    System.out.println("   " + removedTask);
                    System.out.println("Now you have " + tasks.size() + " task(s) in the list.");
                    System.out.println(divider);
                } else if (command.equals("todo") || command.startsWith("todo ")) {
                    String description = command.substring("todo".length()).trim();

                    if (description.isEmpty()) {
                        throw new AlexException("A todo needs a description.");
                    }

                    Task task = new Todo(description);
                    tasks.add(task);

                    System.out.println("Got it. I've added this task:");
                    System.out.println("   " + task);
                    System.out.println("Now you have " + tasks.size() + " task(s) in the list.");
                    System.out.println(divider);
                } else if (command.equals("deadline") || command.startsWith("deadline ")) {
                    String details = command.substring("deadline".length()).trim();
                    int bySeparator = details.indexOf("/by");

                    if (bySeparator < 0) {
                        throw new AlexException("A deadline needs a description and a /by time");
                    }

                    String description = details.substring(0, bySeparator).trim();
                    String by = details.substring(bySeparator + "/by".length()).trim();

                    if (description.isEmpty()) {
                        throw new AlexException("The deadline description cannot be empty.");
                    }

                    if (by.isEmpty()) {
                        throw new AlexException("The deadline time cannot be empty.");
                    }

                    Task task = new Deadline(description, by);
                    tasks.add(task);

                    System.out.println("Got it. I've added this task:");
                    System.out.println("   " + task);
                    System.out.println("Now you have " + tasks.size() + " task(s) in the list.");
                    System.out.println(divider);
                } else if (command.equals("event") || command.startsWith("event ")) {
                    String details = command.substring("event".length()).trim();
                    int fromSeparator = details.indexOf("/from");

                    if (fromSeparator < 0) {
                        throw new AlexException("An event needs a description, a /from time, and a /to time");
                    }

                    int toSeparator = details.indexOf("/to", fromSeparator + "/from".length());

                    if (toSeparator < 0) {
                        throw new AlexException("Please specify the event's end time using /to.");
                    }

                    String description = details.substring(0, fromSeparator).trim();
                    String from = details.substring(fromSeparator + "/from".length(), toSeparator).trim();
                    String to = details.substring(toSeparator + "/to".length()).trim();

                    if (description.isEmpty()) {
                        throw new AlexException("The event description cannot be empty.");
                    }

                    if (from.isEmpty()) {
                        throw new AlexException("The event start time cannot be empty.");
                    }

                    if (to.isEmpty()) {
                        throw new AlexException("The event end time cannot be empty.");
                    }

                    Task task = new Event(description, from, to);
                    tasks.add(task);

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
            }
        }
    }
}
