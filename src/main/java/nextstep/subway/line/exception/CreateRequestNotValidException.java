package nextstep.subway.line.exception;


import nextstep.subway.common.exception.ValidationError;

public class CreateRequestNotValidException extends ValidationError {
    public CreateRequestNotValidException(final String message) {
        super(message);
    }
}
