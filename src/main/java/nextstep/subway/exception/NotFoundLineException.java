package nextstep.subway.exception;

import nextstep.subway.error.ErrorCode;

public class NotFoundLineException extends NextStepException {

    public NotFoundLineException() {
        super(ErrorCode.NOT_FOUND_LINE_ERROR);
    }
}
