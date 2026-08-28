package alex.task;

import java.time.LocalDate;

import alex.util.DateParser;

/**
 * Represents a task that must be completed by a specific date.
 */
public class Deadline extends Task {
    private final LocalDate by;

    /**
     * Creates a deadline with a description and due date.
     *
     * @param description description of the task.
     * @param by date by which the task should be completed.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toDataString() {
        return "D | " + getDoneFlag() + " | " + getDescription() + " | " + by;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateParser.format(by) + ")";
    }
}