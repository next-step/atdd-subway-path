package nextstep.subway.exception;

/**
 * 등록되지 않은 역일 경우 던지는 예외입니다.
 */
public class NotRegisteredStationException extends SubwayException {

    private static final String MESSAGE ="등록된 구간이 아닙니다.";

    public NotRegisteredStationException() {
        super(MESSAGE);
    }
}
