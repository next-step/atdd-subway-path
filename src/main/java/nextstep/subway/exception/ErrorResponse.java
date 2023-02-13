package nextstep.subway.exception;

public class ErrorResponse {
    private int status;
    private String message;

    public ErrorResponse() {
    }

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public static final ErrorResponse of (int status, String message) {
        return new ErrorResponse(status, message);
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
