package nextstep.subway.station.exception;

import nextstep.subway.common.exception.CannotRemoveResourceException;

public class CannotRemoveStationException extends CannotRemoveResourceException {

    public CannotRemoveStationException(String message) {
        super(message);
    }
}
