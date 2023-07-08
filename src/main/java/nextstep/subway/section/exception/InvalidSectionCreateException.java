package nextstep.subway.section.exception;

import subway.support.ErrorCode;
import subway.support.SubwayException;

public class InvalidSectionCreateException extends SubwayException {

    public InvalidSectionCreateException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidSectionCreateException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
