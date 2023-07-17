package nextstep.subway.line;

import nextstep.subway.exception.BusinessException;
import nextstep.subway.exception.ErrorMessage;

public class AlreadyConnectedException extends BusinessException {

    private static final ErrorMessage ERROR_MESSAGE = ErrorMessage.ALREADY_CONNECTED_EXCEPTION;

    public AlreadyConnectedException() {
        super(ERROR_MESSAGE.getMessage());
    }
}
