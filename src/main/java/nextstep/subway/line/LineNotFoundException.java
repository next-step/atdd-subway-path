package nextstep.subway.line;

import nextstep.subway.exception.BusinessException;
import nextstep.subway.exception.ErrorMessage;

public class LineNotFoundException extends BusinessException {

    private static final ErrorMessage ERROR_MESSAGE = ErrorMessage.LINE_NOT_FOUND_EXCEPTION;

    public LineNotFoundException() {
        super(ERROR_MESSAGE.getMessage());
    }
}
