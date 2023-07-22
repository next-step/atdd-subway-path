package subway.exception.impl;

import subway.exception.SubwayException;
import subway.exception.error.SubwayErrorCode;

public class NonLastStationDeleteNotAllowedException extends SubwayException {

    public NonLastStationDeleteNotAllowedException() {
        super(SubwayErrorCode.NON_LAST_STATION_DELETE_NOT_ALLOWED);
    }
}
