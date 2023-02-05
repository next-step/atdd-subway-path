package nextstep.subway.common.exception;

import nextstep.subway.common.error.SubwayError;

public class NoExistStationException extends RuntimeException {

    public NoExistStationException(final SubwayError subwayError) {
        super(subwayError.getMessage());
    }
}