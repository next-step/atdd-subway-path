package nextstep.subway.exception.section;

import nextstep.subway.exception.ServiceException;

public class NotFoundConnectStationException extends ServiceException {

    private static final String MESSAGE = "연결할 역을 찾지 못했습니다.";

    public NotFoundConnectStationException() {
        super(MESSAGE);
    }

}
