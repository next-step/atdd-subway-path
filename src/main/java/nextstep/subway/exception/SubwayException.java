package nextstep.subway.exception;

public abstract class SubwayException extends RuntimeException {
    public SubwayException(String message) {
        super(message);
    }
}
