package nextstep.subway.infra;

import nextstep.subway.domain.exceptions.BusinessException;

public class NotExistsPathException extends BusinessException {
    public NotExistsPathException(String message) {
        super(message);
    }
}
