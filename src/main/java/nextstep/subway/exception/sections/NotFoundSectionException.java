package nextstep.subway.exception.sections;

import nextstep.subway.exception.BusinessException;

public class NotFoundSectionException extends BusinessException {

    private static final String NOT_FOUND_SECTION_EXCEPTION = "해당 구간을 찾을 수 없습니다";

    public NotFoundSectionException() {
        super(NOT_FOUND_SECTION_EXCEPTION);
    }
}
