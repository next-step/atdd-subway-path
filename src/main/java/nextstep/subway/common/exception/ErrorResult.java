package nextstep.subway.common.exception;

public class ErrorResult {
    private final int status;
    private final String message;

    public static ErrorResult from(BusinessException businessException) {
        return new ErrorResult(businessException.getStatus(), businessException.getMessage());
    }

    public ErrorResult(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
