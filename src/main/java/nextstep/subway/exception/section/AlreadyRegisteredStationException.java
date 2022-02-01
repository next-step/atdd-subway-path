package nextstep.subway.exception.section;

import nextstep.subway.exception.ServiceException;

public class AlreadyRegisteredStationException extends ServiceException {

    private static final String MESSAGE = "이미 추가된 구간입니다.";

    public AlreadyRegisteredStationException() {
        super(MESSAGE);
    }

}
