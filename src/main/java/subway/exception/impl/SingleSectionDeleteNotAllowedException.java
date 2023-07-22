package subway.exception.impl;

import subway.exception.SubwayException;
import subway.exception.error.SubwayErrorCode;

public class SingleSectionDeleteNotAllowedException extends SubwayException {

    public SingleSectionDeleteNotAllowedException() {
        super(SubwayErrorCode.SINGLE_SECTION_DELETE_NOT_ALLOWED);
    }
}
