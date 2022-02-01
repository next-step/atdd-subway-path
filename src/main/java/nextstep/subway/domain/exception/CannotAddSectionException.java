package nextstep.subway.domain.exception;

public class CannotAddSectionException extends RuntimeException {

    private static final String EXIST_BOTH_MESSAGE = "추가하려는 역이 이미 노선에 존재합니다";
    private static final String INVALID_UP_STATION = "하행 종점역과 상행역이 일치 하지 않습니다. 하행 종점역 : %s, 상행역 : %s";
    private static final String INVALID_DOWN_STATION = "노선에 이미 등록된 역은 추가할 수 없습니다. %s";

    public CannotAddSectionException() {
        super(EXIST_BOTH_MESSAGE);
    }

    public CannotAddSectionException(String lastStationName, String addUpStationName) {
        super(String.format(INVALID_UP_STATION, lastStationName, addUpStationName));
    }

    public CannotAddSectionException(String containsStationName) {
        super(String.format(INVALID_DOWN_STATION, containsStationName));
    }
}
