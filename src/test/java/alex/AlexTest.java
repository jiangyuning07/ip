package alex;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import alex.storage.Storage;
import alex.storage.StorageException;
import alex.task.Deadline;
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
}
