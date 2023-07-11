package nextstep.subway.ui;

public class ExceptionResponse {

    private String errorMessage;

    public ExceptionResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ExceptionResponse() {
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
