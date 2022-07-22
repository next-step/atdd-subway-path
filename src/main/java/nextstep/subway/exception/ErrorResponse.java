package nextstep.subway.exception;

public class ErrorResponse {

    private int status;
    private String message;

    public ErrorResponse() {
    }

    private ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public static ErrorResponse of(BusinessException e) {
        return new ErrorResponse(e.getStatus().value(), e.getMessage());
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }
}
