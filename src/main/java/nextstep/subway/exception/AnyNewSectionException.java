package nextstep.subway.exception;

public class AnyNewSectionException extends NewSectionException {

    private static final String ERROR_MESSAGE = "정해진 규칙에 맞는 구간을 입력해주세요.";

    public AnyNewSectionException() {
        super(ERROR_MESSAGE);
    }
}
