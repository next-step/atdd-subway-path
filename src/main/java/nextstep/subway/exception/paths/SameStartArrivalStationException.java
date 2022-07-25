package nextstep.subway.exception.paths;

import nextstep.subway.exception.BusinessException;

public class SameStartArrivalStationException extends BusinessException {

    private static final String SAME_START_ARRIVAL_STATION_EXCEPTION = "출발역과 도착역이 같은경우 최단경로를 찾을 수 없습니다";

    public SameStartArrivalStationException() {
        super(SAME_START_ARRIVAL_STATION_EXCEPTION);
    }
}
