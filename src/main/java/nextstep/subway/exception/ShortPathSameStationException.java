package nextstep.subway.exception;

import static nextstep.subway.exception.SubwayError.SHORTPATH_SAME_STATION;

public class ShortPathSameStationException extends SubwayException {
    public ShortPathSameStationException() {
        super(SHORTPATH_SAME_STATION);
    }
}
