package nextstep.subway.line.exception;

public class IsNotValidUpStationException extends RuntimeException {

    public static final String MESSAGE = "상행역은 하행 종점역이어야 합니다.";

    public IsNotValidUpStationException() {
        super(MESSAGE);
    }
}
