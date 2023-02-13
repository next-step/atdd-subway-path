package nextstep.subway.exception;

public class BothSectionStationsNotExistsInLineException extends RuntimeException {

    public static final String MESSAGE = "현재 지하철 노선에, 추가하려는 구간의 상행역[%s]과 하행역[%s]이 모두 등록되어 있지 않습니다.";

    public BothSectionStationsNotExistsInLineException(String upStationName, String downStationName) {
        super(String.format(MESSAGE, upStationName, downStationName));
    }
}
