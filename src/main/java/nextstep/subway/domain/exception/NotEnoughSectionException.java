package nextstep.subway.domain.exception;

public class NotEnoughSectionException extends IllegalStateException {
    public NotEnoughSectionException() {
        super("not enough section for subway line");
    }
}
