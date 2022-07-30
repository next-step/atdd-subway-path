package nextstep.subway.applicaion.exceptions;

import nextstep.subway.enums.exceptions.ErrorCode;

public class DataNotFoundException extends BadRequestException{
    public DataNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
