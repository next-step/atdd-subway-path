package nextstep.subway.applicaion.exceptions;

import nextstep.subway.enums.exceptions.ErrorCode;

public class InvalidStationParameterException extends BadRequestException{
    public InvalidStationParameterException(ErrorCode errorCode) {
        super(errorCode);
    }
}
