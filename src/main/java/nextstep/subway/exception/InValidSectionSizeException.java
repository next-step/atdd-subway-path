package nextstep.subway.exception;

public class InValidSectionSizeException extends BusinessException{

    private static final String ERROR_MESSAGE = "구간은 하나 이상 존재해야 합니다.";

    public InValidSectionSizeException() {
        super(ERROR_MESSAGE);
    }
}
