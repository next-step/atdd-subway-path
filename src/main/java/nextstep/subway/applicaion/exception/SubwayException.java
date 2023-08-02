package nextstep.subway.applicaion.exception;

public class SubwayException extends RuntimeException {

    private final String errorMessage;

    public SubwayException(String errorMessage) {
        super();
        this.errorMessage = errorMessage;
    }

    public String getMessage() {
        return errorMessage;
    }

}