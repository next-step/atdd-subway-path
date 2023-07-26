package nextstep.subway.line;

import nextstep.subway.exception.BusinessException;
import nextstep.subway.exception.ErrorMessage;

public class SectionNotFoundException extends BusinessException {

    private static final ErrorMessage ERROR_MESSAGE = ErrorMessage.SECTION_NOT_FOUND_EXCEPTION;

    public SectionNotFoundException() {
        super(ERROR_MESSAGE.getMessage());
    }
}
