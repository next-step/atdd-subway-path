package nextstep.subway.exception;

import nextstep.subway.error.ErrorCode;

public class DuplicateException extends NextStepException {

    public DuplicateException() {
        super(ErrorCode.DUPLICATE_ERROR);
    }
}
