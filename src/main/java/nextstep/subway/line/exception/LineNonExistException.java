package nextstep.subway.line.exception;

import nextstep.subway.common.exception.NonExistResourceException;

public class LineNonExistException extends NonExistResourceException {

    public LineNonExistException(String message) {
        super(message);
    }
}
