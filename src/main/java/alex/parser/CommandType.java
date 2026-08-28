package alex.parser;

/**
 * Represents a command supported by Alex.
 */
public enum CommandType {
    BYE("bye", false),
    LIST("list", false),
    MARK("mark", true),
    UNMARK("unmark", true),
    DELETE("delete", true),
    TODO("todo", true),
    DEADLINE("deadline", true),
    EVENT("event", true),
    UNKNOWN("", false);

    private final String keyword;
    private final boolean canAcceptArguments;

    /**
     * Creates a command type with its keyword and argument policy.
     *
     * @param keyword text that identifies the command.
     * @param canAcceptArguments whether the command accepts arguments.
     */
    CommandType(String keyword, boolean canAcceptArguments) {
        this.keyword = keyword;
        this.canAcceptArguments = canAcceptArguments;
    }

    /**
     * Returns the keyword that identifies this command type.
     *
     * @return the command keyword.
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Identifies the type of a user command.
     *
     * @param command full user command.
     * @return the matching command type, or {@link #UNKNOWN}.
     */
    public static CommandType parse(String command) {
        for (CommandType type : values()) {
            if (type == UNKNOWN) {
                continue;
            }

            if (command.equals(type.keyword)) {
                return type;
            }

            if (type.canAcceptArguments && command.startsWith(type.keyword + " ")) {
                return type;
            }
        }

        return UNKNOWN;
    }
}
