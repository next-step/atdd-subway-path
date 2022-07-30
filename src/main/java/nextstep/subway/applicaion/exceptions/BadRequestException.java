package nextstep.subway.applicaion.exceptions;

import nextstep.subway.enums.exceptions.ErrorCode;

public abstract class BadRequestException extends RuntimeException{
    private final ErrorCode errorCode;

    public BadRequestException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
