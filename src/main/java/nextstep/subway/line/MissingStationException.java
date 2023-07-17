package nextstep.subway.line;

import nextstep.subway.exception.BusinessException;
import nextstep.subway.exception.ErrorMessage;

public class MissingStationException extends BusinessException {

    private static final ErrorMessage ERROR_MESSAGE = ErrorMessage.MISSING_STATION_EXCEPTION;

    public MissingStationException() {
        super(ERROR_MESSAGE.getMessage());
    }
}
