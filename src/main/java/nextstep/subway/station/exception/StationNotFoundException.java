package nextstep.subway.station.exception;

public class StationNotFoundException extends RuntimeException {

    public StationNotFoundException() {
        super("지하철역이 존재하지 않습니다.");
    }
}
