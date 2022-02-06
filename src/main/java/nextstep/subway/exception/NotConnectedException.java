package nextstep.subway.exception;

import nextstep.subway.error.ErrorCode;

public class NotConnectedException extends NextStepException {

    public NotConnectedException() {
        super(ErrorCode.NOT_CONNECTED_ERROR);
    }
}
