package nextstep.subway.station.exception;

import nextstep.subway.common.exception.NotRemoveResourceException;

public class CannotRemoveStationException extends NotRemoveResourceException {

    public CannotRemoveStationException(String message) {
        super(message);
    }
}
