import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads and saves Alex's task list using a local data file.
 */
public class Storage {
    private final Path filePath;

    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the data file. A missing file represents an empty task list.
     *
     * @return tasks reconstructed from the data file
     * @throws StorageException if an existing file cannot be read or contains invalid data
     */
    public ArrayList<Task> loadTasks() throws StorageException {
        ArrayList<Task> tasks = new ArrayList<>();

        List<String> lines;
        try {
            if (Files.notExists(filePath)) {
                return tasks;
            }
            lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new StorageException("I couldn't read the data file at " + filePath + ".", e);
        }

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }

            try {
                String[] fields = line.split("\\s*\\|\\s*", -1);
                Task task = createTask(fields);

                if (fields[1].equals("1")) {
                    task.markAsDone();
                }
                tasks.add(task);
            } catch (IllegalArgumentException e) {
                throw new StorageException("The data file is invalid at line " + (i + 1)
                        + ": " + e.getMessage(), e);
            }
        }

        return tasks;
    }

    /**
     * Reconstructs the correct task subtype from one line's fields.
     *
     * @param fields fields read from one line in the data file
     * @return the reconstructed task
     */
    private Task createTask(String[] fields) {
        if (fields.length < 2) {
            throw new IllegalArgumentException("missing task type or completion status");
        }

        String taskType = fields[0];
        String status = fields[1];

        if (!status.equals("0") && !status.equals("1")) {
            throw new IllegalArgumentException("completion status must be 0 or 1");
        }

        switch (taskType) {
            case "T":
                validateFields(fields, 3);
                return new Todo(fields[2]);
            case "D":
                validateFields(fields, 4);
                return new Deadline(fields[2], LocalDate.parse(fields[3]));
            case "E":
                validateFields(fields, 5);
                return new Event(fields[2], LocalDate.parse(fields[3]), LocalDate.parse(fields[4]));
            default:
                throw new IllegalArgumentException("unknown task type '" + taskType + "'");
            }
    }

    private void validateFields(String[] fields, int expectedFieldCount) {
        if (fields.length != expectedFieldCount) {
            throw new IllegalArgumentException(
                    "expected " + expectedFieldCount + " fields, but found " + fields.length);
        }

        for (int i = 2; i < fields.length; i++) {
            if (fields[i].isBlank()) {
                throw new IllegalArgumentException("task details cannot be empty");
            }
        }
    }

    /**
     * Replaces the data file with the current task list, creating its directory if necessary.
     *
     * @param tasks current tasks to save
     * @throws StorageException if the directory or file cannot be written
     */
    public void saveTasks(List<Task> tasks) throws StorageException {
        try {
            Files.createDirectories(filePath.getParent());
            List<String> lines = tasks.stream()
                    .map(Task::toDataString)
                    .toList();
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new StorageException("I couldn't save the task list to " + filePath + ".", e);
        }
    }
}
