package alex;

/**
 * Represents the outcome of processing a user command.
 *
 * @param text response text to show the user.
 * @param isError whether the command resulted in an error.
 */
public record CommandResult(String text, boolean isError) {

    /**
     * Creates a successful command result.
     *
     * @param text response text to show the user.
     * @return successful command result.
     */
    public static CommandResult success(String text) {
        return new CommandResult(text, false);
    }

    /**
     * Creates an unsuccessful command result.
     *
     * @param text error text to show the user.
     * @return unsuccessful command result.
     */
    public static CommandResult error(String text) {
        return new CommandResult(text, true);
    }
}
