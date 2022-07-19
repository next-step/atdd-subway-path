package nextstep.subway.domain.exception;

public class NotExistSectionException extends IllegalStateException {

    private final static String MESSAGE = "구간이 존재하지 않습니다.";

    public NotExistSectionException() {
        super(MESSAGE);
    }
}
