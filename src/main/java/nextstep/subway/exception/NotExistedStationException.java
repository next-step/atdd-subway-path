package nextstep.subway.exception;

public class NotExistedStationException extends RuntimeException {
    public NotExistedStationException() {
        super("존재하지 않는 지하철역 입니다.");
    }
}