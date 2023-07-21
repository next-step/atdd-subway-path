package nextstep.subway.section.exception;

import nextstep.subway.common.exception.BusinessException;
import nextstep.subway.common.exception.ErrorCode;

public class SectionNotFoundException extends BusinessException {
    public SectionNotFoundException() {
        super(ErrorCode.SECTION_NOT_FOUND);
    }
}
