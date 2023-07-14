package nextstep.subway.exception;

public class RemoveSectionException extends IllegalArgumentException {

    private static final String ERROR_MESSAGE = "구간이 하나인 노선에서는 구간 삭제가 불가능합니다.";

    public RemoveSectionException() {
        super(ERROR_MESSAGE);
    }
}
