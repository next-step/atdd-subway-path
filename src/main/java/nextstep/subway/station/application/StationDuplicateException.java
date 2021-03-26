package nextstep.subway.station.application;

public class StationDuplicateException extends RuntimeException {
    public StationDuplicateException() {
        super("이미 존재하는 지하철역 이름입니다.");
    }
}
