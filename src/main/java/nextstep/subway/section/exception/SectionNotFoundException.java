package nextstep.subway.section.exception;


import nextstep.subway.support.ErrorCode;
import nextstep.subway.support.SubwayException;

public class SectionNotFoundException extends SubwayException {
    public SectionNotFoundException() {
        super(ErrorCode.SECTION_NOT_FOUND);
    }
}
