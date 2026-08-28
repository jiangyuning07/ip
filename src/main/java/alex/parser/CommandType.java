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

    CommandType(String keyword, boolean canAcceptArguments) {
        this.keyword = keyword;
        this.canAcceptArguments = canAcceptArguments;
    }

    public String getKeyword() {
        return keyword;
    }

    /**
     * Identifies the type of a user command.
     *
     * @param command full user command.
     * @return matching command type, or {@link #UNKNOWN}.
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
