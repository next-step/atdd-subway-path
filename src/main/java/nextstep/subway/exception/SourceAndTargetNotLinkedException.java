package nextstep.subway.exception;

public class SourceAndTargetNotLinkedException extends BadRequestException {
    public SourceAndTargetNotLinkedException(String message) {
        super(message);
    }
}
