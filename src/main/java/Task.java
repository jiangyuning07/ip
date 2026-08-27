public class Task {
    private final String description;
    private boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    public void markAsDone() {
        isDone = true;
    }

    public void markAsUndone() {
        isDone = false;
    }

    /**
     * Returns this task in the format used by the data file.
     *
     * @return the serialized task
     */
    public String toDataString() {
        return "T | " + getDoneFlag() + " | " + description;
    }

    protected int getDoneFlag() {
        return isDone ? 1 : 0;
    }

    protected String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
