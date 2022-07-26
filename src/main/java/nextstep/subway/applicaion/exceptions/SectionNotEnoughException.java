package nextstep.subway.applicaion.exceptions;

import nextstep.subway.enums.exceptions.ErrorCode;

public class SectionNotEnoughException extends BadRequestException{
    public SectionNotEnoughException(ErrorCode errorCode) {
        super(errorCode);
    }
}
