package nextstep.subway.domain.exception;

public class CannotDeleteSectionException extends RuntimeException {

    private static final String MINIMUM_LIMIT_MESSAGE = "노선의 구간은 1개 이상 존재해야 합니다.";
    private static final String NOT_EXISTS_MESSAGE = "노선에 없는 역은 삭제할 수 없습니다. [%s]";

    public CannotDeleteSectionException() {
        super(MINIMUM_LIMIT_MESSAGE);
    }

    public CannotDeleteSectionException(String stationName) {
        super(String.format(NOT_EXISTS_MESSAGE, stationName));
    }

}
