package nextstep.subway.line.exception;

public class IsDownStationExistedException extends RuntimeException {

    public static final String MESSAGE = "하행역이 이미 등록되어 있습니다.";

    public IsDownStationExistedException() {
        super(MESSAGE);
    }

    public IsDownStationExistedException(String message) {
        super(message);
    }
}
