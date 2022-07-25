package nextstep.subway.exception.sections;

import nextstep.subway.exception.BusinessException;

public class NotFoundBothStationException extends BusinessException {

    private static final String NOT_FOUND_BOTH_STATION_EXCEPTION = "상행역과 하행역 모두 찾을 수 없습니다";

    public NotFoundBothStationException() {
        super(NOT_FOUND_BOTH_STATION_EXCEPTION);
    }
}
