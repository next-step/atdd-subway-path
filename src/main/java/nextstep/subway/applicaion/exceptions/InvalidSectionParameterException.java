package nextstep.subway.applicaion.exceptions;

import nextstep.subway.enums.exceptions.ErrorCode;

public class InvalidSectionParameterException extends BadRequestException{
    public InvalidSectionParameterException(ErrorCode errorCode) {
        super(errorCode);
    }
}
