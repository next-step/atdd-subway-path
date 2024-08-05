package nextstep.subway.line.exception;

import nextstep.subway.common.exception.SubwayException;
import nextstep.subway.common.exception.SubwayExceptionType;

public class InvalidSectionLengthException extends SubwayException {

    public InvalidSectionLengthException(Long distance) {
        super(SubwayExceptionType.INVALID_SECTION_LENGTH,
            String.format(SubwayExceptionType.INVALID_SECTION_LENGTH.getMessage(), distance));
    }
}
