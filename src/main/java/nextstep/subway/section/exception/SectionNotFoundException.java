package nextstep.subway.section.exception;

import subway.support.ErrorCode;
import subway.support.SubwayException;

public class SectionNotFoundException extends SubwayException {
    public SectionNotFoundException() {
        super(ErrorCode.SECTION_NOT_FOUND);
    }
}
