package nextstep.subway.common.exception;

import nextstep.subway.common.error.SubwayError;

public class AlreadyExistException extends RuntimeException {

    public AlreadyExistException(final SubwayError subwayError) {
        super(subwayError.getMessage());
    }
}