package nextstep.subway.exception;

public class MinimumSizeRemoveSectionException extends RemoveSectionException {

    private static final String ERROR_MESSAGE = "구간이 하나인 노선에서는 구간 삭제가 불가능합니다.";

    public MinimumSizeRemoveSectionException() {
        super(ERROR_MESSAGE);
    }
}
