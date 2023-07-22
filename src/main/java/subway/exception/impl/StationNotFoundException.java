package subway.exception.impl;

import subway.exception.SubwayException;
import subway.exception.error.SubwayErrorCode;

public class StationNotFoundException extends SubwayException {

    public StationNotFoundException() {
        super(SubwayErrorCode.STATION_NOT_FOUND);
    }
}
