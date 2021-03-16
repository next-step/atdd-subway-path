package nextstep.subway.station.exception;

public class DoseNotExistedStationException extends RuntimeException {
    public static final String MESSAGE = "존재하지 않은 역에 대한 요청입니다.";

    public DoseNotExistedStationException() {
        super(MESSAGE);
    }
}
