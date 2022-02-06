package nextstep.subway.handler.validator;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.handler.exception.StationException;

import static nextstep.subway.handler.exception.ErrorCode.STATION_NOT_EXISTS_IN_LINE;

public class StationValidator {
    public static void validateStationRemove(Line line, Station station) {
        if (!line.hasStation(station)) {
            throw new StationException(STATION_NOT_EXISTS_IN_LINE);
        }
    }
}
