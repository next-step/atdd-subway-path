package nextstep.subway.common.exception;

import nextstep.subway.common.error.SubwayError;

public class NoPathConnectedException extends RuntimeException {

    public NoPathConnectedException(final SubwayError subwayError) {
        super(subwayError.getMessage());
    }
}
