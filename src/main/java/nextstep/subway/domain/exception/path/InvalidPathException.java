package nextstep.subway.domain.exception.path;

import nextstep.subway.domain.exception.BusinessException;

public class InvalidPathException extends BusinessException {

    private static final String MESSAGE = PathErrorMessage.INVALID_PATH.getMessage();

    public InvalidPathException() {
        super(MESSAGE);
    }
}
