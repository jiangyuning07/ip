package alex.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import alex.task.Deadline;
import alex.task.Event;
import alex.task.Task;
import alex.util.TaskDateTime;

/**
 * Tests task persistence behavior.
 */
public class StorageTest {
    @TempDir
    private Path tempDirectory;

    @Test
    public void saveAndLoadTasks_optionalTimes_preservesDatesAndTimes() throws StorageException {
        Storage storage = new Storage(tempDirectory.resolve("alex.txt"));
        List<Task> originalTasks = List.of(
                new Deadline("date only", LocalDate.of(2019, 12, 2)),
                new Deadline("timed", new TaskDateTime(
                        LocalDate.of(2019, 12, 2), LocalTime.of(18, 0))),
                new Event("mixed event",
                        new TaskDateTime(LocalDate.of(2019, 12, 2)),
                        new TaskDateTime(LocalDate.of(2019, 12, 3), LocalTime.of(9, 30))));

        storage.saveTasks(originalTasks);
        List<String> loadedTaskData = storage.loadTasks().stream()
                .map(Task::toDataString)
                .toList();

        assertEquals(originalTasks.stream().map(Task::toDataString).toList(), loadedTaskData);
    }
}
