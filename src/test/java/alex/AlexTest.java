package alex;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import alex.storage.Storage;
import alex.storage.StorageException;
import alex.task.Deadline;
import alex.task.Task;
import alex.task.Todo;
import alex.util.TaskDateTime;

/**
 * Tests command execution behavior.
 */
public class AlexTest {
    @TempDir
    private Path tempDirectory;

    @Test
    public void getResponse_upcomingCommand_returnsUpcomingDeadlines() throws StorageException {
        Path dataFile = tempDirectory.resolve("alex.txt");
        Storage storage = new Storage(dataFile);
        storage.saveTasks(List.of(
                new Deadline("submit report", new TaskDateTime(
                        LocalDate.of(2026, 9, 16), LocalTime.of(18, 0))),
                new Deadline("later task", new TaskDateTime(
                        LocalDate.of(2026, 9, 18), LocalTime.of(12, 0)))));
        Clock clock = Clock.fixed(
                Instant.parse("2026-09-16T04:00:00Z"), ZoneId.of("Asia/Singapore"));
        Alex alex = new Alex(dataFile.toString(), clock);

        CommandResult result = alex.getResponse("upcoming");

        assertEquals(new CommandResult(
                "Here are your incomplete deadlines due in the next 24 hours:\n"
                        + " 1.[D][ ] submit report (by: Sep 16 2026 1800)",
                false), result);
    }

    @Test
    public void getResponse_upcomingCommandWithoutMatches_returnsEmptyMessage() {
        Clock clock = Clock.fixed(
                Instant.parse("2026-09-16T04:00:00Z"), ZoneId.of("Asia/Singapore"));
        Alex alex = new Alex(tempDirectory.resolve("alex.txt").toString(), clock);

        CommandResult result = alex.getResponse("upcoming");

        assertEquals(new CommandResult(
                "You have no incomplete deadlines due in the next 24 hours.", false), result);
    }

    @Test
    public void getResponse_findCommandWithoutMatches_returnsError() {
        Alex alex = new Alex(tempDirectory.resolve("alex.txt").toString());

        CommandResult result = alex.getResponse("find report");

        assertEquals(new CommandResult("Sorry! No matches found.", true), result);
    }

    @Test
    public void getResponse_addWhenSaveFails_doesNotAddTask() {
        Alex alex = createAlexWithSaveFailure(List.of());

        CommandResult result = alex.getResponse("todo read book");

        assertEquals(new CommandResult("Sorry! Simulated save failure.", true), result);
        assertEquals(new CommandResult("Here are the tasks in your list:", false),
                alex.getResponse("list"));
    }

    @Test
    public void getResponse_markWhenSaveFails_doesNotMarkTask() {
        Alex alex = createAlexWithSaveFailure(List.of(new Todo("read book")));

        CommandResult result = alex.getResponse("mark 1");

        assertEquals(new CommandResult("Sorry! Simulated save failure.", true), result);
        assertEquals(new CommandResult(
                "Here are the tasks in your list:\n 1.[T][ ] read book", false),
                alex.getResponse("list"));
    }

    @Test
    public void getResponse_unmarkWhenSaveFails_doesNotUnmarkTask() {
        Task task = new Todo("read book");
        task.markAsDone();
        Alex alex = createAlexWithSaveFailure(List.of(task));

        CommandResult result = alex.getResponse("unmark 1");

        assertEquals(new CommandResult("Sorry! Simulated save failure.", true), result);
        assertEquals(new CommandResult(
                "Here are the tasks in your list:\n 1.[T][X] read book", false),
                alex.getResponse("list"));
    }

    @Test
    public void getResponse_deleteWhenSaveFails_doesNotDeleteTask() {
        Alex alex = createAlexWithSaveFailure(List.of(new Todo("read book")));

        CommandResult result = alex.getResponse("delete 1");

        assertEquals(new CommandResult("Sorry! Simulated save failure.", true), result);
        assertEquals(new CommandResult(
                "Here are the tasks in your list:\n 1.[T][ ] read book", false),
                alex.getResponse("list"));
    }

    private Alex createAlexWithSaveFailure(List<Task> initialTasks) {
        return new Alex(new SaveFailingStorage(tempDirectory.resolve("alex.txt"), initialTasks),
                Clock.systemDefaultZone());
    }

    private static class SaveFailingStorage extends Storage {
        private final List<Task> initialTasks;

        SaveFailingStorage(Path filePath, List<Task> initialTasks) {
            super(filePath);
            this.initialTasks = initialTasks;
        }

        @Override
        public ArrayList<Task> loadTasks() {
            return new ArrayList<>(initialTasks);
        }

        @Override
        public void saveTasks(List<Task> tasks) throws StorageException {
            throw new StorageException("Simulated save failure.");
        }
    }
}
