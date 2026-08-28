package alex.task;

import java.time.LocalDate;

import alex.util.DateParser;

/**
 * Represents a task that occurs over a date range.
 */
public class Event extends Task {
    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * Creates an event with a description and date range.
     *
     * @param description description of the event.
     * @param startDate event start date.
     * @param endDate event end date.
     */
    public Event(String description, LocalDate startDate, LocalDate endDate) {
        super(description);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toDataString() {
        return "E | " + getDoneFlag() + " | " + getDescription()
                + " | " + startDate + " | " + endDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + DateParser.format(startDate)
                + " to: " + DateParser.format(endDate) + ")";
    }
}
