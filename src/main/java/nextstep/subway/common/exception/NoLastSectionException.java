package nextstep.subway.common.exception;

import nextstep.subway.common.error.SubwayError;

public class NoLastSectionException extends RuntimeException {

    public NoLastSectionException(final SubwayError subwayError) {
        super(subwayError.getMessage());
    }
}