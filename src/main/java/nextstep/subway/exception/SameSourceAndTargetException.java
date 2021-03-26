package nextstep.subway.exception;

public class SameSourceAndTargetException extends BusinessException{

    private final static String ERROR_MESSAGE = "출발역과 도착역이 같을 수 없습니다.";

    public SameSourceAndTargetException() {
        super(ERROR_MESSAGE);
    }
}
