package nextstep.subway.section.exception;


import nextstep.subway.support.ErrorCode;
import nextstep.subway.support.SubwayException;

public class InvalidSectionDeleteException extends SubwayException {
    public InvalidSectionDeleteException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidSectionDeleteException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
