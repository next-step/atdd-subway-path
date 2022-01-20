package nextstep.subway.line.exception;

public abstract class LineDomainException extends RuntimeException {
    public LineDomainException(String message) {
        super(message);
    }
}
