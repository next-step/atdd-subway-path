package nextstep.subway.line;

import nextstep.subway.exception.BusinessException;
import nextstep.subway.exception.ErrorMessage;

public class StationNotIncludedException extends BusinessException {

    private static final ErrorMessage ERROR_MESSAGE = ErrorMessage.STATION_NOT_INCLUDED_EXCEPTION;

    public StationNotIncludedException() {
        super(ERROR_MESSAGE.getMessage());
    }
}
