package nextstep.subway.domain.exception;

public class CannotDeleteSectionException extends RuntimeException {

    private static final String MINIMUM_LIMIT_MESSAGE = "노선의 구간은 1개 이상 존재해야 합니다.";
    private static final String NOT_EXISTS_MESSAGE = "노선에 없는 역은 삭제할 수 없습니다. [%s]";

    private CannotDeleteSectionException() {
        super(MINIMUM_LIMIT_MESSAGE);
    }

    private CannotDeleteSectionException(String stationName) {
        super(String.format(NOT_EXISTS_MESSAGE, stationName));
    }

    public static CannotDeleteSectionException minimumLimit() {
        return new CannotDeleteSectionException();
    }

    public static CannotDeleteSectionException notExist(String stationName) {
        return new CannotDeleteSectionException(stationName);
    }

}
