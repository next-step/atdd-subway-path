package nextstep.subway.exception;

import static java.lang.String.format;

public class InvalidSectionDistanceException extends RuntimeException {

    public InvalidSectionDistanceException(int newSectionDistance, int oldSectionDistance) {
        super(format("new section distance '%s' must be lower than old section distance '%s",
                newSectionDistance,
                oldSectionDistance));
    }
}
