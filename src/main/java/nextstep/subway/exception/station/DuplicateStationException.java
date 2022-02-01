package nextstep.subway.exception.station;

import nextstep.subway.exception.ServiceException;

public class DuplicateStationException extends ServiceException {

    private static final String MESSAGE = "역 이름이 중복 됩니다. - %s";

    public DuplicateStationException(String stationName) {
        super(String.format(MESSAGE, stationName));
    }

}
