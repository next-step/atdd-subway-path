package nextstep.subway.line.exception;

import nextstep.subway.common.exception.NonExistResourceException;

public class SectionNonExistException extends NonExistResourceException {

    public SectionNonExistException(String message) {
        super(message);
    }
}
