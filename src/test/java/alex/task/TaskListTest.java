package alex.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task-list search behavior.
 */
public class TaskListTest {
    /**
     * Verifies that tasks containing a keyword in their descriptions are returned.
     */
    @Test
    public void find_keywordInDescriptions_returnsMatchingTasks() {
        Task readBook = new Todo("read book");
        Task returnBook = new Deadline("return book", LocalDate.of(2026, 6, 6));
        Task unrelatedTask = new Todo("buy groceries");
        TaskList tasks = new TaskList(List.of(readBook, returnBook, unrelatedTask));

        assertEquals(List.of(readBook, returnBook), tasks.find("book"));
    }

    /**
     * Verifies that an absent keyword produces an empty result.
     */
    @Test
    public void find_keywordAbsent_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertEquals(List.of(), tasks.find("movie"));
    }
}
