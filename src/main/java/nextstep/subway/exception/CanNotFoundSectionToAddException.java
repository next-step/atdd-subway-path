package nextstep.subway.exception;

public class CanNotFoundSectionToAddException extends BusinessException{

    private static final String ERROR_MESSAGE = "추가할 수 있는 구간이 존재하지 않습니다.";

    public CanNotFoundSectionToAddException() {
        super(ERROR_MESSAGE);
    }
}
