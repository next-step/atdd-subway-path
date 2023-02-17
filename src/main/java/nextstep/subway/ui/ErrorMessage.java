package nextstep.subway.ui;

public class ErrorMessage {
    private String message;

    static ErrorMessage of(String message) {
        return new ErrorMessage(message);
    }

    public ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
