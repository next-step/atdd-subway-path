package nextstep.subway.line.exception;

import nextstep.subway.common.exception.NotRemoveResourceException;

public class CannotRemoveSectionException extends NotRemoveResourceException {

    public CannotRemoveSectionException(String message) {
        super(message);
    }
}
