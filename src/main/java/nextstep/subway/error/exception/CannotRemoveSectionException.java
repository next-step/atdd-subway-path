package nextstep.subway.error.exception;

public class CannotRemoveSectionException extends RuntimeException {
    private static final String MESSAGE = "역을 삭제 할 수 없습니다.";

    public CannotRemoveSectionException() {
        super(MESSAGE);
    }
}
