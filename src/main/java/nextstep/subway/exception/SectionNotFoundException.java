package nextstep.subway.exception;

public class SectionNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final String message = "일치하는 구간이 없습니다.";

    public SectionNotFoundException() {
        super(message);
    }
}
