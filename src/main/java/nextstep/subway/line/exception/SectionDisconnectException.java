package nextstep.subway.line.exception;

import nextstep.subway.common.exception.BadRequestException;

public class SectionDisconnectException extends BadRequestException {
    public SectionDisconnectException(final String message) {
        super(message);
    }
}
