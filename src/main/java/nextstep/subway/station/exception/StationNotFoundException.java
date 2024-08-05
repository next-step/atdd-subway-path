package nextstep.subway.station.exception;

import nextstep.subway.common.exception.SubwayException;
import nextstep.subway.common.exception.SubwayExceptionType;

public class StationNotFoundException extends SubwayException {

    public StationNotFoundException(Long stationId) {
        super(SubwayExceptionType.STATION_NOT_FOUND,
            String.format(SubwayExceptionType.STATION_NOT_FOUND.getMessage(), stationId));
    }
}
