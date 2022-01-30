package nextstep.subway.exception.line;

public class LineNotFoundException extends RuntimeException{

    private static final String MESSAGE = "노선을 찾을 수 없습니다.";

    public LineNotFoundException() {
        super(MESSAGE);
    }

}
