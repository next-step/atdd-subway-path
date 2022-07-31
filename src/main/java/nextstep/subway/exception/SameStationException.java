package nextstep.subway.exception;

public class SameStationException extends RuntimeException {

    public SameStationException() {
        super("출발역과 도착역이 동일합니다.");
    }

}
