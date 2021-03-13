package nextstep.subway.station.exception;

import nextstep.subway.common.exception.NonExistResourceException;

public class StationNonExistException extends NonExistResourceException {

    public StationNonExistException(String message) {
        super(message);
    }
}
