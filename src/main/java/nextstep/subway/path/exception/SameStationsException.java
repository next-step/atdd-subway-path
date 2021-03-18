package nextstep.subway.path.exception;

public class SameStationsException extends RuntimeException {

    public SameStationsException() {
        super("출발역과 도착역은 달라야 합니다.");
    }
}
