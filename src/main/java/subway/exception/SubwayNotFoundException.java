package subway.exception;

import subway.constant.SubwayMessage;

public class SubwayNotFoundException extends SubwayException {

    public SubwayNotFoundException(SubwayMessage subwayMessage) {
        super(subwayMessage);
    }

    public SubwayNotFoundException(final long code, final String message) {
        super(code, message);
    }
}
