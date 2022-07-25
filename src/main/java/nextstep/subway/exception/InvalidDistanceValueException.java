package nextstep.subway.exception;

public class InvalidDistanceValueException extends RuntimeException {

    private static final String INVALID_DISTANCE_VALUE_EXCEPTION = "distance 값은 0보다 크거나 같아야 합니다 (요청한 distance: %d)";

    public InvalidDistanceValueException(int distance) {
        super(String.format(INVALID_DISTANCE_VALUE_EXCEPTION, distance));
    }

}