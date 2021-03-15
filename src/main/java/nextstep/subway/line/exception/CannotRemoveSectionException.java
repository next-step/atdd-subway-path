package nextstep.subway.line.exception;

import nextstep.subway.common.exception.CannotRemoveResourceException;

public class CannotRemoveSectionException extends CannotRemoveResourceException {

    public CannotRemoveSectionException(String message) {
        super(message);
    }
}
