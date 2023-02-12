package nextstep.subway.domain.exception;

public class AddSectionConstraintException extends IllegalArgumentException {
    private static final String MESSAGE = "구간 추가 제약 조건을 위반했습니다.";

    public AddSectionConstraintException() {
        super(MESSAGE);
    }
}
