package nextstep.subway.line.exception;

public class SectionNotFoundException extends RuntimeException {
    public SectionNotFoundException(String msg) {
        super(msg);
    }
}
