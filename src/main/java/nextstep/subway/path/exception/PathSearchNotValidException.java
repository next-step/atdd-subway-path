package nextstep.subway.path.exception;


import nextstep.subway.common.exception.ValidationError;

public class PathSearchNotValidException extends ValidationError {
    public PathSearchNotValidException(final String message) {
        super(message);
    }
}
