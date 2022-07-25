package nextstep.subway.exception.sections;

import nextstep.subway.exception.BusinessException;

public class NotFoundStationException extends BusinessException {

    private static final String NOT_FOUND_STATION_EXCEPTION = "해당 역을 찾을 수 없습니다";

    public NotFoundStationException() {
        super(NOT_FOUND_STATION_EXCEPTION);
    }
}
