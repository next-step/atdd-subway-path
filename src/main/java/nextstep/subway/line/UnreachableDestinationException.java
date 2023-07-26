package nextstep.subway.line;

import nextstep.subway.exception.BusinessException;
import nextstep.subway.exception.ErrorMessage;

public class UnreachableDestinationException extends BusinessException {

    private static final ErrorMessage ERROR_MESSAGE = ErrorMessage.UNREACHABLE_DESTINATION_EXCEPTION;

    public UnreachableDestinationException() {
        super(ERROR_MESSAGE.getMessage());
    }

}
