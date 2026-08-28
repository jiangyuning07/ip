package alex.task;

import java.time.LocalDate;

import alex.util.DateParser;

/**
 * Represents a task that occurs over a date range.
 */
public class Event extends Task {
    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates an event with a description and date range.
     *
     * @param description description of the event.
     * @param from event start date.
     * @param to event end date.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toDataString() {
        return "E | " + getDoneFlag() + " | " + getDescription() + " | " + from + " | " + to;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + DateParser.format(from)
                + " to: " + DateParser.format(to) + ")";
    }
}