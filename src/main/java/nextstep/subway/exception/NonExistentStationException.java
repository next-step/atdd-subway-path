package nextstep.subway.exception;

public class NonExistentStationException extends RuntimeException {
    public NonExistentStationException() {
        super("존재하지 않는 지하철역입니다.");
    }
}
