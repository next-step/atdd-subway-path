package nextstep.subway.error.exception;

public enum ErrorCode {
    INVALID_INPUT_VALUE(400, " Invalid Input Value"),
    ENTITY_NOT_FOUND(400, " Entity Not Found"),
    INTERNAL_SERVER_ERROR(500, "Server Error"),
    ENTITY_DUPLICATION(400, " Entity is duplicate"),
    CANNOT_REMOVE_STATE(400, " Connot remove state"),
    LINES_EMPTY(400, "Lines are empty")
    ;

    private final String message;
    private final int status;

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }
}
