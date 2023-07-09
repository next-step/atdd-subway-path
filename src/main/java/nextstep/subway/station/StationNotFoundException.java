package nextstep.subway.station;

import nextstep.subway.exception.BusinessException;
import nextstep.subway.exception.ErrorMessage;

public class StationNotFoundException extends BusinessException {

    private static final ErrorMessage ERROR_MESSAGE = ErrorMessage.LINE_NOT_FOUND_EXCEPTION;

    public StationNotFoundException() {
        super(ERROR_MESSAGE.getMessage());
    }
}
