package alex.task;

import java.time.LocalDate;

import alex.util.DateParser;

public class Deadline extends Task {
    private final LocalDate by;

    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toDataString() {
        return "D | " + getDoneFlag() + " | " + getDescription() + " | " + by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateParser.format(by) + ")";
    }
}