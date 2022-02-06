package nextstep.subway.exception.dto;

public class CommonExceptionResponse {
    private String message;

    public CommonExceptionResponse(Exception exception) {
        this.message = exception.getLocalizedMessage();
    }

    public String getMessage() {
        return message;
    }
}
