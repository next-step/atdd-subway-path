package nextstep.subway.exception;

public class InvalidDistanceException extends IllegalArgumentException {
    public InvalidDistanceException(int currentDistance) {
        super("exceeded the allowable range. Please enter a value between 0 and " + currentDistance + ".");
    }
}
