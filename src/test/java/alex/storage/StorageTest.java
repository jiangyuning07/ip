package alex.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

    @Test
    public void loadTasks_eventEndsBeforeStart_storageExceptionThrown() throws IOException {
        Path dataFile = tempDirectory.resolve("alex.txt");
        Files.writeString(dataFile,
                "E | 0 | meeting | 2019-12-02 1800 | 2019-12-02 1700",
                StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        StorageException exception = assertThrows(StorageException.class, storage::loadTasks);

        assertEquals("The data file is invalid at line 1: "
                + "The event end must be after its start.", exception.getMessage());
    }
}
