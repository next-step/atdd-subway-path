package nextstep.subway.line;

import nextstep.subway.exception.BusinessException;
import nextstep.subway.exception.ErrorMessage;

public class NonDownstreamTerminusException extends BusinessException {

    private static final ErrorMessage ERROR_MESSAGE = ErrorMessage.NON_DOWNSTREAM_TERMINUS_EXCEPTION;

    public NonDownstreamTerminusException() {
        super(ERROR_MESSAGE.getMessage());
    }
}
