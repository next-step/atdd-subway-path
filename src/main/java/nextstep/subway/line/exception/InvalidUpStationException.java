package nextstep.subway.line.exception;

import nextstep.subway.common.exception.SubwayException;
import nextstep.subway.common.exception.SubwayExceptionType;

public class InvalidUpStationException extends SubwayException {

    public InvalidUpStationException(Long stationId) {
        super(SubwayExceptionType.INVALID_UP_STATION,
            String.format(SubwayExceptionType.INVALID_UP_STATION.getMessage(), stationId));
    }
}
