package nextstep.subway.common.exception;

import nextstep.subway.common.error.SubwayError;

public class NoDeleteOneSectionException extends RuntimeException {

    public NoDeleteOneSectionException(final SubwayError subwayError) {
        super(subwayError.getMessage());
    }
}