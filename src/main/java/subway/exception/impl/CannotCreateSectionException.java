package subway.exception.impl;

import subway.exception.SubwayException;
import subway.exception.error.SubwayErrorCode;

public class CannotCreateSectionException extends SubwayException {

    public CannotCreateSectionException() {
        super(SubwayErrorCode.CANNOT_CREATE_SECTION);
    }
}
