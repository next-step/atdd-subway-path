package nextstep.subway.domain.exception;

public class StationsNotConnectedException extends RuntimeException {
    public StationsNotConnectedException(String message) {
        super(message);
    }
}
