package nextstep.subway.exception;

public class IllegalNewSectionException extends IllegalArgumentException {

    private static final String MSG = "정해진 규칙에 맞는 구간을 입력해주세요.";

    public IllegalNewSectionException() {
        super(MSG);
    }
}
