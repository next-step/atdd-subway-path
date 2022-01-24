package nextstep.subway.line.exception;

public class NotLastStationException extends LineDomainException {
    private static final String MESSAGE = "상행역은 하행 종점역이어야 합니다.";

    public NotLastStationException() {
        super(MESSAGE);
    }
}
