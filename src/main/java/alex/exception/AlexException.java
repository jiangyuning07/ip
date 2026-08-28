package alex.exception;

/**
 * Signals that a user command cannot be completed.
 */
public class AlexException extends Exception {
    public AlexException(String message) {
        super(message);
    }
}
