package nextstep.subway.exception;

import static nextstep.subway.exception.SubwayError.STATION_NOT_EXIST_EXCEPTION;

public class StationNotExistException extends SubwayException {
    public StationNotExistException() {
        super(STATION_NOT_EXIST_EXCEPTION);
    }
}
