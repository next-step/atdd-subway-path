package nextstep.subway.error.exception;

public class LinesEmptyException extends BusinessException {
    public LinesEmptyException() {
        super(ErrorCode.LINES_EMPTY);
    }
}
