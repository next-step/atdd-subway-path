package nextstep.subway.path.exception;

public abstract class PathDomainException extends RuntimeException {
    public PathDomainException(String message) {
        super(message);
    }
}
