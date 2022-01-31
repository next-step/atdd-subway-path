package nextstep.subway.exception.section;

public class NotFoundConnectStationException extends RuntimeException {

    private static final String MESSAGE = "연결할 역을 찾지 못했습니다.";

    public NotFoundConnectStationException() {
        super(MESSAGE);
    }

}
