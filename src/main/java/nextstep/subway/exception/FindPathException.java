package nextstep.subway.exception;

public class FindPathException extends RuntimeException {
    public FindPathException(ErrorType errorType) {
        super(errorType.getMessage());
    }
}