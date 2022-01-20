package nextstep.subway.line.exception;

public class NotLastStationException extends LineDomainException {
    private static String message = "상행역은 하행 종점역이어야 합니다.";

    public NotLastStationException() {
        super(message);
    }
}
