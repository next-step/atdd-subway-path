package nextstep.subway.exception;

public class DuplicatedDownStationException extends IllegalArgumentException {

    private static final String DUPLICATED_DOWN_STATION = "새로운 구간의 하행역은 해당 노선에 등록되어있는 역일 수 없습니다.";

    public DuplicatedDownStationException() {
        super(DUPLICATED_DOWN_STATION);
    }
}
