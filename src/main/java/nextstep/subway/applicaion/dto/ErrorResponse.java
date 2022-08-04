package nextstep.subway.applicaion.dto;

public class ErrorResponse {
    private int httpStatusCode;
    private String errorMessage;

    public ErrorResponse(int httpStatusCode, String errorMessage) {
        this.httpStatusCode = httpStatusCode;
        this.errorMessage = errorMessage;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
