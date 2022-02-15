package nextstep.subway.exception;

public class CannotRemoveSection extends RuntimeException {
    private static final String MESSAGE = "역을 삭제 할 수 없습니다.";

    public CannotRemoveSection() {
        super(MESSAGE);
    }
}
