package nextstep.subway.exception;

public class ColorNotAvailableException extends SubwayException {
    public ColorNotAvailableException() {
        super(SubwayError.COLOR_NOT_AVAILABLE);
    }
}
