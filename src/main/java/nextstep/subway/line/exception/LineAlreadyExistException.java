package nextstep.subway.line.exception;

import nextstep.subway.common.exception.ExistResourceException;

public class LineAlreadyExistException extends ExistResourceException {

    public LineAlreadyExistException(String message) {
        super(message);
    }
}
