package nextstep.subway.section.exception;

import subway.support.ErrorCode;
import subway.support.SubwayException;

public class InvalidSectionDeleteException extends SubwayException {
    public InvalidSectionDeleteException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidSectionDeleteException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
