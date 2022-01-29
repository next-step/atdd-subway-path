package nextstep.subway.exception;

public class LineNotFoundException extends RuntimeException{
    public static final String EXCEPTION_MESSAGE = "노선을 찾을 수 없습니다.";

    public LineNotFoundException() {
        super(EXCEPTION_MESSAGE);
    }
}
