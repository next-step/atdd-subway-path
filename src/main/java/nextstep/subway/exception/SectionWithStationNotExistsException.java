package nextstep.subway.exception;

public class SectionWithStationNotExistsException extends RuntimeException {

    public static final String MESSAGE = "지하철 노선에 해당 [%s] 역을 포함하는 구간이 존재하지 않습니다.";

    public SectionWithStationNotExistsException(String stationName) {
        super(String.format(MESSAGE, stationName));
    }
}
