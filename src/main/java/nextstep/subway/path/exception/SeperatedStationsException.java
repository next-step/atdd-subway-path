package nextstep.subway.path.exception;

public class SeperatedStationsException extends RuntimeException {

    public SeperatedStationsException() {
        super("출발역이나 도착역이 연결되어 있지 않습니다.");
    }
}
