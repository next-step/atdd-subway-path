package nextstep.subway.infra;

import nextstep.subway.domain.exceptions.BusinessException;

public class SameStationPathException extends BusinessException {
    public SameStationPathException(String message) {
        super(message);
    }
}
