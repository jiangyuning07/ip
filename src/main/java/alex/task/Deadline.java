package alex.task;

import java.time.LocalDate;

import alex.util.DateParser;

/**
 * Represents a task that must be completed by a specific date.
 */
public class Deadline extends Task {
    private final LocalDate dueDate;

    /**
     * Creates a deadline with a description and due date.
     *
     * @param description description of the task.
     * @param dueDate date by which the task should be completed.
     */
    public Deadline(String description, LocalDate dueDate) {
        super(description);
        this.dueDate = dueDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toDataString() {
        return "D | " + getDoneFlag() + " | " + getDescription() + " | " + dueDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateParser.format(dueDate) + ")";
    }
}
