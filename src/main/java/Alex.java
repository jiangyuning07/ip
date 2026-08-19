import java.util.Scanner;

public class Alex {
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
        Task[] tasks = new Task[100];
        int taskCount = 0;

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(divider);

            if (command.equals("bye")) {
                System.out.println(farewell);
                System.out.println(divider);
                break;
            } else if (command.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(" " + (i + 1) + "." + tasks[i]);
                }
                System.out.println(divider);
            } else if (command.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(command.substring(5).trim());
                int taskIndex = taskNumber - 1;
                Task task = tasks[taskIndex];
                task.markAsDone();

                System.out.println("Nice! I've marked this task as done:");
                System.out.println("   " + task);
                System.out.println(divider);
            } else if (command.startsWith("unmark ")) {
                int taskNumber = Integer.parseInt(command.substring(7).trim());
                int taskIndex = taskNumber - 1;
                Task task = tasks[taskIndex];
                task.markAsUndone();

                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("   " + task);
                System.out.println(divider);
            } else if (command.startsWith("todo ")) {
                Task task = new Todo(command.substring("todo ".length()));
                tasks[taskCount] = task;
                taskCount++;

                System.out.println("Got it. I've added this task:");
                System.out.println("  " + task);
                System.out.println("Now you have " + taskCount + " tasks in the list.");
                System.out.println(divider);
            } else if (command.startsWith("deadline ")) {
                String details = command.substring("deadline ".length());
                int bySeparator = details.indexOf(" /by ");

                String description = details.substring(0, bySeparator);
                String by = details.substring(bySeparator + " /by ".length());

                Task task = new Deadline(description, by);
                tasks[taskCount] = task;
                taskCount++;

                System.out.println("Got it. I've added this task:");
                System.out.println("  " + task);
                System.out.println("Now you have " + taskCount + " tasks in the list.");
                System.out.println(divider);
            } else if (command.startsWith("event ")) {
                String details = command.substring("event ".length());
                int fromSeparator = details.indexOf(" /from ");
                int toSeparator = details.indexOf(" /to ", fromSeparator + " /from ".length());

                String description = details.substring(0, fromSeparator);
                String from = details.substring(fromSeparator + " /from ".length(), toSeparator);
                String to = details.substring(toSeparator + " /to ".length());

                Task task = new Event(description, from, to);
                tasks[taskCount] = task;
                taskCount++;

                System.out.println("Got it. I've added this task:");
                System.out.println("  " + task);
                System.out.println("Now you have " + taskCount + " tasks in the list.");
                System.out.println(divider);
            } else {
                Task task = new Task(command);
                tasks[taskCount] = task;
                taskCount++;

                System.out.println("added: " + task);
                System.out.println(divider);
            }
        }
    }
}
