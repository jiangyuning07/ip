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
    private final boolean accArgs;

    CommandType(String keyword, boolean accArgs) {
        this.keyword = keyword;
        this.accArgs = accArgs;
    }

    public String getKeyword() {
        return keyword;
    }

    public static CommandType from(String command) {
        for (CommandType type : values()) {
            if (type == UNKNOWN) {
                continue;
            }

            if (command.equals(type.keyword)) {
                return type;
            }

            if (type.accArgs && command.startsWith(type.keyword + " ")) {
                return type;
            }
        }

        return UNKNOWN;
    }
}