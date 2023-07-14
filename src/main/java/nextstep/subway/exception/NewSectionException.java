package nextstep.subway.exception;

public class NewSectionException extends RuntimeException {

    private static final String MSG = "정해진 규칙에 맞는 구간을 입력해주세요.";

    public NewSectionException() {
        super(MSG);
    }

    public NewSectionException(String message) {
        super(message);
    }
}
