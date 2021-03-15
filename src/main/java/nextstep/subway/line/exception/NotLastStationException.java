package nextstep.subway.line.exception;

public class NotLastStationException extends RuntimeException {

    public NotLastStationException() {
        super("하행 종점역만 삭제가 가능합니다.");
    }
}
