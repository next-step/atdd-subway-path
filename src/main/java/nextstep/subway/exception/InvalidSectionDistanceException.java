package nextstep.subway.exception;

public class InvalidSectionDistanceException extends BusinessException{

    private static final String ERROR_MESSAGE = "등록되는 구간은 기존 구간보다 작아야 합니다.";

    public InvalidSectionDistanceException() {
        super(ERROR_MESSAGE);
    }
}
