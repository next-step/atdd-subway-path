package nextstep.subway.common.exception;

import nextstep.subway.common.error.SubwayError;

public class NoExistLineException extends RuntimeException {

    public NoExistLineException(final SubwayError subwayError) {
        super(subwayError.getMessage());
    }
}