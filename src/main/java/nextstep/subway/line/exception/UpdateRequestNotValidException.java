package nextstep.subway.line.exception;


import nextstep.subway.common.exception.ValidationError;

public class UpdateRequestNotValidException extends ValidationError {
    public UpdateRequestNotValidException(final String message) {
        super(message);
    }
}
