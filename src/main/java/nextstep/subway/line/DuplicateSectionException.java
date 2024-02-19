package nextstep.subway.line;

public class DuplicateSectionException extends RuntimeException {
    public DuplicateSectionException(String msg) {
        super("중복 구간입니다. " + msg);
    }
}
