package nextstep.subway.exception;

public class NotMatchDeleteSectionException extends RuntimeException {
    private static final String MESSAGE = "삭제하고자 하는 역과 노선의 상행역이 일치하지 않으면 삭제가 불가능함";

    public NotMatchDeleteSectionException() {
        super(MESSAGE);
    }
}

