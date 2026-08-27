package alex.task;

import java.time.LocalDate;

import alex.util.DateParser;

public class Event extends Task {
    private final LocalDate from;
    private final LocalDate to;

    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toDataString() {
        return "E | " + getDoneFlag() + " | " + getDescription() + " | " + from + " | " + to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + DateParser.format(from)
                + " to: " + DateParser.format(to) + ")";
    }
}