package nextstep.subway.line;

import nextstep.subway.exception.BusinessException;
import nextstep.subway.exception.ErrorMessage;

public class SameStationException extends BusinessException {

    private static final ErrorMessage ERROR_MESSAGE = ErrorMessage.SAME_STATION_EXCEPTION;

    public SameStationException() {
        super(ERROR_MESSAGE.getMessage());
    }

}
