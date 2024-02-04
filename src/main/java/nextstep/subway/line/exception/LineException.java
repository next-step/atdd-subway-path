package nextstep.subway.line.exception;

public class LineException extends RuntimeException {

    public LineException() {
    }

    public LineException(String s) {
        super(s);
    }

    public LineException(String message, Throwable cause) {
        super(message, cause);
    }

    public LineException(Throwable cause) {
        super(cause);
    }
}
