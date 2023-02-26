package nextstep.subway.infra;

import nextstep.subway.domain.exceptions.BusinessException;

public class CanNotFindStationInPathException extends BusinessException {
    public CanNotFindStationInPathException(String message) {
        super(message);
    }
}
