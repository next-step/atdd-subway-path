package nextstep.subway.global;

public class ErrorResponse {

    private String message;

    private ErrorResponse(String message) {
        this.message = message;
    }

    public static ErrorResponse of(String message) {
        return new ErrorResponse(message);
    }

    public String getMessage() {
        return message;
    }
}
