package alex.task;

import java.time.LocalDate;

import alex.util.DateParser;

/**
 * Represents a task that occurs over a date range.
 */
public class Event extends Task {
    private final LocalDate startDate;
    private final LocalDate endDate;

    public Event(String description, LocalDate startDate, LocalDate endDate) {
        super(description);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public String toDataString() {
        return "E | " + getDoneFlag() + " | " + getDescription()
                + " | " + startDate + " | " + endDate;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + DateParser.format(startDate)
                + " to: " + DateParser.format(endDate) + ")";
    }
}
