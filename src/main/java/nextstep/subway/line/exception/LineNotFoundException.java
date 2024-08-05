package nextstep.subway.line.exception;

import nextstep.subway.common.exception.SubwayException;
import nextstep.subway.common.exception.SubwayExceptionType;

public class LineNotFoundException extends SubwayException {

    public LineNotFoundException(Long lineId) {
        super(SubwayExceptionType.LINE_NOT_FOUND,
            String.format(SubwayExceptionType.LINE_NOT_FOUND.getMessage(), lineId));
    }
}
