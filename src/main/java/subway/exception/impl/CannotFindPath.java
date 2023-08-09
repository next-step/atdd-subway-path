package subway.exception.impl;

import subway.exception.SubwayException;
import subway.exception.error.SubwayErrorCode;

public class CannotFindPath extends SubwayException {

    public CannotFindPath() {
        super(SubwayErrorCode.CANNOT_FIND_PATH);
    }
}
