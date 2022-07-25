package nextstep.subway.exception;

public class NonConnectionStationException extends IllegalArgumentException {
    public NonConnectionStationException() {
        super("source and target are not connected.");
    }
}
