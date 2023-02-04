package nextstep.subway.common;

public class ErrorResponse {

    private String message;

    public ErrorResponse(final String message) {
        super();
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
