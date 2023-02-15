package nextstep.subway.common.exception;

import nextstep.subway.common.error.SubwayError;

public class SameStationException extends RuntimeException {

    public SameStationException(final SubwayError subwayError) {
        super(subwayError.getMessage());
    }
}
