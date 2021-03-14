package nextstep.subway.line.exception;

public class IsExistedSectionException extends RuntimeException {

    public static final String MESSAGE = "이미 등록된 구간입니다.";

    public IsExistedSectionException() {
        super(MESSAGE);
    }

    public IsExistedSectionException(String message) {
        super(message);
    }
}
