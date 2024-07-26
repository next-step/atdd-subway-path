package nextstep.subway.path.exceptions;

import nextstep.subway.exceptions.BaseException;
import nextstep.subway.exceptions.ErrorMessage;

public class PathNotFoundException extends BaseException {
    public PathNotFoundException(final ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
