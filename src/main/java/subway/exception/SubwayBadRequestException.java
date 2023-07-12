package subway.exception;

import subway.line.constant.SubwayMessage;

public class SubwayBadRequestException extends SubwayException {

    public SubwayBadRequestException(SubwayMessage subwayMessage) {
        super(subwayMessage);
    }

    public SubwayBadRequestException(final long code, final String message) {
        super(code, message);
    }
}
