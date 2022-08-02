package nextstep.subway.exception;

public class SourceAndTargetSameException extends BadRequestException {
    public SourceAndTargetSameException(String message) {
        super(message);
    }
}
