package nextstep.subway.exception;

public class SameOriginPathException extends PathException {

    private static final String ERROR_MESSAGE = "출발역과 도착역이 같습니다.";

    public SameOriginPathException() {
        super(ERROR_MESSAGE);
    }
}
