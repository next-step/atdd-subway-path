package nextstep.subway.exception;

public class InvalidDistanceOfSectionException extends RuntimeException {
    private String errorMessage;

    public InvalidDistanceOfSectionException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
