package nextstep.subway.exceptions;

public class NotFoundLineException extends RuntimeException{
    public final static String DEFAULT_MSG = "라인을 찾을 수 없습니다.";
    public final static String DEFAULT_MSG_WITH_LINE_NO = "%d번 라인을 찾을 수 없습니다.";

    public NotFoundLineException() {
        super(DEFAULT_MSG);
    }

    public NotFoundLineException(Long id) {
        super(String.format(DEFAULT_MSG_WITH_LINE_NO, id));
    }

    public NotFoundLineException(String message) {
        super(message);
    }
}
