package nextstep.subway.exception;

public class DuplicateSectionException extends IllegalArgumentException {
    public DuplicateSectionException() {
        super("duplicate section");
    }
}
