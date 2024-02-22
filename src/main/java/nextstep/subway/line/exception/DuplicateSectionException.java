package nextstep.subway.line.exception;

public class DuplicateSectionException extends RuntimeException {
    public DuplicateSectionException(String msg) {
        super("중복 구간입니다. " + msg);
    }
}
