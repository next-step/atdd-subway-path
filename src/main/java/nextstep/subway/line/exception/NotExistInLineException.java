package nextstep.subway.line.exception;

public class NotExistInLineException extends RuntimeException{
    private static final String MESSAGE = "일치하는 상행역 혹은 하행역이 구간에 없습니다.";

    public NotExistInLineException() {
        super(MESSAGE);
    }
}
