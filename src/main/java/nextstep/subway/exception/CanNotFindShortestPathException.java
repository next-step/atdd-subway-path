package nextstep.subway.exception;

public class CanNotFindShortestPathException extends IllegalArgumentException {
    public CanNotFindShortestPathException(String message) {
        super(message);
    }

    public CanNotFindShortestPathException(String message, IllegalArgumentException e) {
        super(message, e);
    }
}
