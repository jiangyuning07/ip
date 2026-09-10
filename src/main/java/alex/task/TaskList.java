package alex.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Manages the tasks currently held by Alex.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing copies of the supplied references.
     *
     * @param tasks initial tasks.
     */
    public TaskList(Task... tasks) {
        assert tasks != null : "Initial task array cannot be null";

        this.tasks = new ArrayList<>(List.of(tasks));
    }

    /**
     * Creates a task list containing copies of the supplied references.
     *
     * @param tasks initial tasks.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Initial task list cannot be null";
        assert !tasks.contains(null) : "Initial task list cannot contain null";

        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the task count.
     */
    public int getSize() {
        return tasks.size();
    }

    /**
     * Returns the task at the specified zero-based index.
     *
     * @param index zero-based task index.
     * @return the task at the index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add.
     */
    public void add(Task task) {
        assert task != null : "Added task cannot be null";

        tasks.add(task);
    }

    /**
     * Removes and returns the task at the specified zero-based index.
     *
     * @param index zero-based task index.
     * @return the removed task.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns tasks whose descriptions contain the specified keyword, ignoring case.
     *
     * @param keyword keyword to search for.
     * @return matching tasks in their original order.
     */
    public List<Task> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);

        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .toList();
    }

    /**
     * Returns an unmodifiable snapshot of the tasks.
     *
     * @return current tasks.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }
}
