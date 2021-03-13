package nextstep.subway.exceptions;

public class InvalidSectionException extends RuntimeException {
    public static final String DEFAULT_MSG = "잘못된 구간입니다.";

    public InvalidSectionException() {
        super(DEFAULT_MSG);
    }

    public InvalidSectionException(String message) {
        super(message);
    }
}
