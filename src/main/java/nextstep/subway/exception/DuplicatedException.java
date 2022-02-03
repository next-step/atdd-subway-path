package nextstep.subway.exception;

public class DuplicatedException extends IllegalArgumentException {
    public DuplicatedException() {
        super();
    }

    public DuplicatedException(String message) {
        super(message);
    }

}
