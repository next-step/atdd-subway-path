package nextstep.subway.station.exception;

import nextstep.subway.support.ErrorCode;
import nextstep.subway.support.SubwayException;

public class StationNotFoundException extends SubwayException {
    public StationNotFoundException() {
        super(ErrorCode.STATION_NOT_FOUND);
    }
}
