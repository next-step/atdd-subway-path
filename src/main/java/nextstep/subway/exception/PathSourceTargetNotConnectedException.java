package nextstep.subway.exception;

public class PathSourceTargetNotConnectedException extends RuntimeException {
    public PathSourceTargetNotConnectedException(String message) {
        super(message);
    }

    public PathSourceTargetNotConnectedException(String message, Throwable cause) {
        super(message, cause);
    }
}
