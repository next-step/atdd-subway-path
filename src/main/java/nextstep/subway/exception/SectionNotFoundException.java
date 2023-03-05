package nextstep.subway.exception;

public class SectionNotFoundException extends RuntimeException {
    private static final String MESSAGE = "노선을 찾을 수 없습니다.";

    public SectionNotFoundException() {
        super(MESSAGE);
    }
}
