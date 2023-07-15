package nextstep.subway.exception;

public class NotExistedRemoveSectionException extends RemoveSectionException {

    private static final String ERROR_MESSAGE = "입력한 역이 등록된 구간이 존재하지 않습니다.";

    public NotExistedRemoveSectionException() {
        super(ERROR_MESSAGE);
    }
}
