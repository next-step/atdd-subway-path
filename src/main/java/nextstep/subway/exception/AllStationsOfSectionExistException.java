package nextstep.subway.exception;

public class AllStationsOfSectionExistException extends RuntimeException {
    private String errorMessage;

    public AllStationsOfSectionExistException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}