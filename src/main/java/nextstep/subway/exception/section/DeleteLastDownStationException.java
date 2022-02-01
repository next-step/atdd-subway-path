package nextstep.subway.exception.section;

import nextstep.subway.exception.ServiceException;

public class DeleteLastDownStationException extends ServiceException {

    private static final String MESSAGE = "마지막역(하행 종점역)만 삭제 가능합니다. - %s";

    public DeleteLastDownStationException(String stationName) {
        super(String.format(MESSAGE, stationName));

    }

}
