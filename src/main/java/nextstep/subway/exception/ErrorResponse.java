package nextstep.subway.exception;

import nextstep.subway.exception.sections.SectionsException;

public class ErrorResponse {

    private int status;
    private String message;

    public ErrorResponse() {
    }

    private ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public static ErrorResponse of(SectionsException e) {
        return new ErrorResponse(e.getErrorCode().getStatus().value(), e.getMessage());
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }
}
