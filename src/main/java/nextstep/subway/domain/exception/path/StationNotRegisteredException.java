package nextstep.subway.domain.exception.path;

import nextstep.subway.domain.exception.BusinessException;

public class StationNotRegisteredException extends BusinessException {

    private static final String MESSAGE = PathErrorMessage.STATION_NOT_REGISTERED.getMessage();

    public StationNotRegisteredException() {
        super(MESSAGE);
    }
}
