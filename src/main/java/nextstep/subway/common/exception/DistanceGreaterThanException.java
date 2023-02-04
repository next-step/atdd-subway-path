package nextstep.subway.common.exception;

public class DistanceGreaterThanException extends RuntimeException {

    public DistanceGreaterThanException(final String message) {
        super(message);
    }
}