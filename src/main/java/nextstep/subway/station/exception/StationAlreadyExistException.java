package nextstep.subway.station.exception;

import nextstep.subway.common.exception.ExistResourceException;

public class StationAlreadyExistException extends ExistResourceException {

    public StationAlreadyExistException(String message) {
        super(message);
    }
}
