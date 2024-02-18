package nextstep.subway.line;

public class SectionNotFoundException extends RuntimeException {
    public SectionNotFoundException(String msg) {
        super(msg);
    }
}
