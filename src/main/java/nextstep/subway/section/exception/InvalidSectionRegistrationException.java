package nextstep.subway.section.exception;

import nextstep.subway.common.exception.BusinessException;
import nextstep.subway.common.exception.ErrorCode;

public class InvalidSectionRegistrationException extends BusinessException {
    public InvalidSectionRegistrationException() {
        super(ErrorCode.INVALID_SECTION_REGISTRATION);
    }
}
