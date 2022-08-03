package nextstep.subway.exception;

public class NonStationOfSectionExistsException extends RuntimeException {
    private String errorMessage;

    public NonStationOfSectionExistsException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
