package nextstep.subway.line.exception;

public class DoseNotExistStationOfSectionException extends RuntimeException {

    public static final String MESSAGE = "상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.";

    public DoseNotExistStationOfSectionException() {
        super(MESSAGE);
    }
}
