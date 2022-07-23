package nextstep.subway.exception;

public class NotFoundLineException extends IllegalArgumentException {
    public NotFoundLineException() {
        super("not found line that corresponding to station");
    }
}
