package alex.task;

import java.time.LocalDate;

import alex.util.DateParser;
import alex.util.TaskDateTime;

/**
 * Represents a task that must be completed by a specific date.
 */
public class Deadline extends Task {
    private final TaskDateTime dueDateTime;

    /**
     * Creates a deadline with a description and due date.
     *
     * @param description description of the task.
     * @param dueDate date by which the task should be completed.
     */
    public Deadline(String description, LocalDate dueDate) {
        this(description, new TaskDateTime(dueDate));
    }

    /**
     * Creates a deadline with a description, due date, and optional time.
     *
     * @param description description of the task.
     * @param dueDateTime date and optional time by which the task should be completed.
     */
    public Deadline(String description, TaskDateTime dueDateTime) {
        super(description);
        assert dueDateTime != null : "Deadline due date and time cannot be null";

        this.dueDateTime = dueDateTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toDataString() {
        return "D | " + getDoneFlag() + " | " + getDescription()
                + " | " + DateParser.formatForStorage(dueDateTime);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateParser.format(dueDateTime) + ")";
    }
}
