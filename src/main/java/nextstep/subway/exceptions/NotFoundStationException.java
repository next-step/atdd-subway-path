package nextstep.subway.exceptions;

public class NotFoundStationException extends RuntimeException {
    public static final String DEFAULT_MSG = "지하철역을 찾을 수 없습니다.";
    public static final String MSG_WITH_SECTION = "%s 지하철역을 찾을 수 없습니다.";

    public NotFoundStationException() {
        super(DEFAULT_MSG);
    }

    public NotFoundStationException(String contents) {
        super(String.format(MSG_WITH_SECTION, contents));
    }
}
