package nextstep.subway.exception;

public class DuplicateSectionException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final String message = "이미 등록된 구간이 있습니다.";

    public DuplicateSectionException() {
        super(message);
    }
}
