package nextstep.subway.common.exception;

import nextstep.subway.common.error.SubwayError;

public class NoRegisterStationException extends RuntimeException {

    public NoRegisterStationException(final SubwayError subwayError) {
        super(subwayError.getMessage());
    }
}