package nextstep.subway.section.exception;


import nextstep.subway.support.ErrorCode;
import nextstep.subway.support.SubwayException;

public class InvalidSectionCreateException extends SubwayException {

    public InvalidSectionCreateException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidSectionCreateException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
