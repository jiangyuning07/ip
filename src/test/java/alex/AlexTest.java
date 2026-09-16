package alex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
    public void getResponse_taskCreationCommands_persistsAllTaskTypes() {
        Path dataFile = tempDirectory.resolve("alex.txt");
        Alex alex = new Alex(dataFile.toString());

        assertEquals(new CommandResult(
                "Got it. I've added this task:\n"
                        + "   [T][ ] read book\n"
                        + "Now you have 1 task(s) in the list.", false),
                alex.getResponse("todo read book"));
        assertEquals(new CommandResult(
                "Got it. I've added this task:\n"
                        + "   [D][ ] submit report (by: Dec 2 2019 1800)\n"
                        + "Now you have 2 task(s) in the list.", false),
                alex.getResponse("deadline submit report /by 2019-12-02 1800"));
        assertEquals(new CommandResult(
                "Got it. I've added this task:\n"
                        + "   [E][ ] meeting (from: Dec 3 2019 0900 to: Dec 3 2019 1000)\n"
                        + "Now you have 3 task(s) in the list.", false),
                alex.getResponse("event meeting /from 2019-12-03 0900 /to 2019-12-03 1000"));

        Alex reloadedAlex = new Alex(dataFile.toString());
        assertEquals(new CommandResult(
                "Here are the tasks in your list:\n"
                        + " 1.[T][ ] read book\n"
                        + " 2.[D][ ] submit report (by: Dec 2 2019 1800)\n"
                        + " 3.[E][ ] meeting (from: Dec 3 2019 0900 to: Dec 3 2019 1000)",
                false), reloadedAlex.getResponse("list"));
    }

    @Test
    public void getResponse_taskStateCommands_persistChangesAndOrder() throws StorageException {
        Path dataFile = tempDirectory.resolve("alex.txt");
        Storage storage = new Storage(dataFile);
        storage.saveTasks(List.of(
                new Todo("first task"),
                new Todo("second task"),
                new Todo("third task")));
        Alex alex = new Alex(dataFile.toString());

        assertEquals(new CommandResult(
                "Nice! I've marked this task as done:\n   [T][X] second task", false),
                alex.getResponse("mark 2"));
        assertTrue(storage.loadTasks().get(1).isDone());

        Alex reloadedAfterMark = new Alex(dataFile.toString());
        assertEquals(new CommandResult(
                "OK, I've marked this task as not done yet:\n   [T][ ] second task", false),
                reloadedAfterMark.getResponse("unmark 2"));
        assertFalse(storage.loadTasks().get(1).isDone());

        Alex reloadedAfterUnmark = new Alex(dataFile.toString());
        assertEquals(new CommandResult(
                "Noted. I've removed this task:\n"
                        + "   [T][ ] second task\n"
                        + "Now you have 2 task(s) in the list.", false),
                reloadedAfterUnmark.getResponse("delete 2"));

        Alex reloadedAfterDelete = new Alex(dataFile.toString());
        assertEquals(new CommandResult(
                "Here are the tasks in your list:\n"
                        + " 1.[T][ ] first task\n"
                        + " 2.[T][ ] third task", false),
                reloadedAfterDelete.getResponse("list"));
    }

    @Test
    public void getResponse_simpleCommands_returnsExpectedResults() throws StorageException {
        Path dataFile = tempDirectory.resolve("alex.txt");
        Storage storage = new Storage(dataFile);
        storage.saveTasks(List.of(new Todo("read book")));
        Alex alex = new Alex(dataFile.toString());

        assertEquals(new CommandResult(
                "Here are the matching tasks in your list:\n 1.[T][ ] read book", false),
                alex.getResponse("find book"));
        assertEquals(new CommandResult("Bye. Hope to see you again soon!", false),
                alex.getResponse("bye"));
        assertEquals(new CommandResult("Sorry! I don't recognize that command.", true),
                alex.getResponse("dance"));
    }

    @Test
    public void getResponse_invalidStoredTask_returnsLoadingError() throws IOException {
        Path dataFile = tempDirectory.resolve("alex.txt");
        Files.writeString(dataFile, "X | 0 | invalid task", StandardCharsets.UTF_8);
        Alex alex = new Alex(dataFile.toString());

        CommandResult result = alex.getResponse("list");

        assertEquals(new CommandResult(
                "Sorry! The data file is invalid at line 1: unknown task type 'X'\n"
                        + "Please repair or remove the data file, then restart Alex.",
                true), result);
    }

    @Test
    public void getResponse_blankCommand_returnsSpecificError() {
        Alex alex = new Alex(tempDirectory.resolve("alex.txt").toString());

        CommandResult result = alex.getResponse("   ");

        assertEquals(new CommandResult("Sorry! Please enter a command.", true), result);
    }

    @Test
    public void getResponse_listCommandWithDetails_returnsSpecificError() {
        Alex alex = new Alex(tempDirectory.resolve("alex.txt").toString());

        CommandResult result = alex.getResponse("list extra details");

        assertEquals(new CommandResult(
                "Sorry! The 'list' command does not accept additional details.", true), result);
    }

    @Test
    public void getResponse_upcomingCommandWithDetails_returnsSpecificError() {
        Alex alex = new Alex(tempDirectory.resolve("alex.txt").toString());

        CommandResult result = alex.getResponse("upcoming extra details");

        assertEquals(new CommandResult(
                "Sorry! The 'upcoming' command does not accept additional details.", true), result);
    }

    @Test
    public void getResponse_byeCommandWithDetails_returnsSpecificError() {
        Alex alex = new Alex(tempDirectory.resolve("alex.txt").toString());

        CommandResult result = alex.getResponse("bye extra details");

        assertEquals(new CommandResult(
                "Sorry! The 'bye' command does not accept additional details.", true), result);
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
        Alex alex = createAlexWithSaveFailure(List.of(
                new Todo("first task"),
                new Todo("second task"),
                new Todo("third task")));

        CommandResult result = alex.getResponse("delete 2");

        assertEquals(new CommandResult("Sorry! Simulated save failure.", true), result);
        assertEquals(new CommandResult(
                "Here are the tasks in your list:\n"
                        + " 1.[T][ ] first task\n"
                        + " 2.[T][ ] second task\n"
                        + " 3.[T][ ] third task", false),
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
