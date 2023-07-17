package nextstep.subway.line;

import nextstep.subway.exception.BusinessException;
import nextstep.subway.exception.ErrorMessage;

public class InvalidDistanceException extends BusinessException {

    private static final ErrorMessage ERROR_MESSAGE = ErrorMessage.INVALID_DISTANCE_EXCEPTION;

    public InvalidDistanceException() {
        super(ERROR_MESSAGE.getMessage());
    }
}
