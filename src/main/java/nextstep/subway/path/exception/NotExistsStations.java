package nextstep.subway.path.exception;

public class NotExistsStations extends RuntimeException {

    public NotExistsStations() {
        super("출발역이나 도착역이 존재하지 않습니다.");
    }
}
