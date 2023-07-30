package nextstep.subway.exception;

import static nextstep.subway.exception.SubwayError.NOT_FOUND_STATION;

public class StationNotFoundException extends SubwayException {
    public StationNotFoundException() {
        super(NOT_FOUND_STATION);
    }
}
