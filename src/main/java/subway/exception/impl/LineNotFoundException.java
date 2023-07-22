package subway.exception.impl;

import subway.exception.SubwayException;
import subway.exception.error.SubwayErrorCode;

public class LineNotFoundException extends SubwayException {

    public LineNotFoundException() {
        super(SubwayErrorCode.LINE_NOT_FOUND);
    }
}
