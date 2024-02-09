package nextstep.subway.line.exception;

import nextstep.subway.common.exception.BadStateException;

public class SectionsStateException extends BadStateException {
    public SectionsStateException(final String message) {
        super(message);
    }
}
