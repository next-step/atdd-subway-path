package nextstep.subway.exception;

public class IdenticalSectionAlreadyExistsInLineException extends RuntimeException {

    private static final String MESSAGE = "지하철 노선에 동일한 지하철 구간이 존재합니다 (상행역: %s, 하행역: %s)";

    public IdenticalSectionAlreadyExistsInLineException(String upStationName, String downStationName) {
        super(String.format(MESSAGE, upStationName, downStationName));
    }
}
