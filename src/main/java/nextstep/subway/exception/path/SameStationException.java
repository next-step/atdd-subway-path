package nextstep.subway.exception.path;

import nextstep.subway.exception.ServiceException;

public class SameStationException extends ServiceException {

    private static final String MESSAGE = "출발역과 도착역을 다르게 선택해 주세요.";

    public SameStationException() {
        super(MESSAGE);
    }

}
