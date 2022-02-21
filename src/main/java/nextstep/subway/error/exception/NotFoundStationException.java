package nextstep.subway.error.exception;

public class NotFoundStationException extends RuntimeException {
    private static final String MESSAGE = "역을 찾을 수 없습니다.";

    public NotFoundStationException() {
        super(MESSAGE);
    }
}
