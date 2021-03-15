package nextstep.subway.exception;

public class NoOtherStationException extends RuntimeException {
    public NoOtherStationException() {
        super("역이 하나이므로 제거 될 수 없습니다.");
    }
}
