package nextstep.subway.exception;

public class SameStationException extends IllegalArgumentException {
    public SameStationException() {
        super("source station and target station are the same");
    }
}
