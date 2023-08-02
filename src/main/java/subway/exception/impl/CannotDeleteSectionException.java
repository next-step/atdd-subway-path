package subway.exception.impl;

import subway.exception.SubwayException;
import subway.exception.error.SubwayErrorCode;

public class CannotDeleteSectionException extends SubwayException {

    public CannotDeleteSectionException() {
        super(SubwayErrorCode.CANNOT_DELETE_SECTION);
    }
}
