package nextstep.subway.path.exception;

public class FirstStationEqualsFinalStationException extends RuntimeException {
    public FirstStationEqualsFinalStationException() {
        super("출발역과 도착역은 같을 수 없습니다");
    }
}
