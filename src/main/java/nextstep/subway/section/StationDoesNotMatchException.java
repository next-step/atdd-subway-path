package nextstep.subway.section;

public class StationDoesNotMatchException extends RuntimeException {
    public StationDoesNotMatchException(String stationName) {
        super(stationName + "  역은 노선의 끝역이 아닙니다.");
    }
}
