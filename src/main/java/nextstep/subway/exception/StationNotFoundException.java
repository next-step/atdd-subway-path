package nextstep.subway.exception;

public class StationNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final String message = "일치하는 역이 없습니다.";

    public StationNotFoundException() {
        super(message);
    }

    public StationNotFoundException(String message) {
        super(message);
    }
}
