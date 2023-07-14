package nextstep.subway.exception;

public class LineNotFoundException extends NotFoundException {

    private static final String ERROR_MESSAGE = "Line 이 존재하지 않습니다.";

    public LineNotFoundException() {
        super(ERROR_MESSAGE);
    }

    public LineNotFoundException(String message) {
        super(message);
    }
}
