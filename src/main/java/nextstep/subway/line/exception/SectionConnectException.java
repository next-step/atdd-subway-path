package nextstep.subway.line.exception;

import nextstep.subway.common.exception.BadRequestException;

public class SectionConnectException extends BadRequestException {
    public SectionConnectException(final String message) {
        super(message);
    }
}
