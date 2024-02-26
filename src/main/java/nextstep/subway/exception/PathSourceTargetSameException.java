package nextstep.subway.exception;

public class PathSourceTargetSameException extends RuntimeException {
    public PathSourceTargetSameException(String message) {
        super(message);
    }

    public PathSourceTargetSameException(String message, Throwable cause) {
        super(message, cause);
    }
}
