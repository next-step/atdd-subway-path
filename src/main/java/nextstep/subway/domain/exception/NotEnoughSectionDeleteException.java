package nextstep.subway.domain.exception;

public class NotEnoughSectionDeleteException extends IllegalStateException {
    private static final String MESSAGE = "최소 두개 이상의 구간이 있어야 삭제가 가능합니다.";

    public NotEnoughSectionDeleteException() {
        super(MESSAGE);
    }
}
