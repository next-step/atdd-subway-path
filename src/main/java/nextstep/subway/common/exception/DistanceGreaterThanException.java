package nextstep.subway.common.exception;

import nextstep.subway.common.error.SubwayError;

public class DistanceGreaterThanException extends RuntimeException {

    public DistanceGreaterThanException(final SubwayError subwayError) {
        super(subwayError.getMessage());
    }
}