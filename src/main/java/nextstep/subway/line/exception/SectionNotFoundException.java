package nextstep.subway.line.exception;

public class SectionNotFoundException extends IllegalArgumentException{

    public SectionNotFoundException(String msg) {
        super(msg);
    }
}
