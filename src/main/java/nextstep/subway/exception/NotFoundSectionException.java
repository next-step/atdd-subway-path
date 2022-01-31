package nextstep.subway.exception;

import nextstep.subway.error.ErrorCode;

public class NotFoundSectionException extends NextStepException {

    public NotFoundSectionException() {
        super(ErrorCode.NOT_FOUND_SECTION_ERROR);
    }
}
