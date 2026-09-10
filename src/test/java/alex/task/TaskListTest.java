package alex.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task-list search behavior.
 */
public class TaskListTest {
    @Test
    public void find_partialKeywordInDescriptions_returnsMatchingTasks() {
        Task readBook = new Todo("read book");
        Task returnBook = new Deadline("return book", LocalDate.of(2026, 6, 6));
        Task unrelatedTask = new Todo("buy groceries");
        TaskList tasks = new TaskList(readBook, returnBook, unrelatedTask);

        assertEquals(List.of(readBook, returnBook), tasks.find("boo"));
    }

    @Test
    public void find_keywordHasDifferentCase_returnsMatchingTasks() {
        Task readBook = new Todo("read book");
        TaskList tasks = new TaskList(readBook, new Todo("buy groceries"));

        assertEquals(List.of(readBook), tasks.find("BOOK"));
    }

    @Test
    public void find_keywordAbsent_returnsEmptyList() {
        TaskList tasks = new TaskList(new Todo("read book"));

        assertEquals(List.of(), tasks.find("movie"));
    }
}
