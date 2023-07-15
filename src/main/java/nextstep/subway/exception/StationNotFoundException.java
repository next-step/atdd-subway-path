package nextstep.subway.exception;

public class StationNotFoundException extends NotFoundException {

    private static final String ERROR_MESSAGE = "Station 이 존재하지 않습니다.";

    public StationNotFoundException() {
        super(ERROR_MESSAGE);
    }

    public StationNotFoundException(String message) {
        super(message);
    }
}
