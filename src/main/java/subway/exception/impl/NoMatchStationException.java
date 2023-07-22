package subway.exception.impl;

import subway.exception.SubwayException;
import subway.exception.error.SubwayErrorCode;

public class NoMatchStationException extends SubwayException {

    public NoMatchStationException() {
        super(SubwayErrorCode.NO_MATCH_UP_STATION);
    }

}
