package nextstep.subway.station;

public class StationNotFoundException extends RuntimeException {

    public StationNotFoundException(String msg) {
        super("역 정보 없음 : " + msg);
    }
}
