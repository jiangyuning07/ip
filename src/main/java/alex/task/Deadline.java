package alex.task;

import java.time.LocalDate;

import alex.util.DateParser;

/**
 * Represents a task that must be completed by a specific date.
 */
public class Deadline extends Task {
    private final LocalDate dueDate;

    public Deadline(String description, LocalDate dueDate) {
        super(description);
        this.dueDate = dueDate;
    }

    @Override
    public String toDataString() {
        return "D | " + getDoneFlag() + " | " + getDescription() + " | " + dueDate;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateParser.format(dueDate) + ")";
    }
}
