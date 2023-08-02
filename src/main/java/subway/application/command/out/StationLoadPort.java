package subway.application.command.out;

import subway.domain.Station;

public interface StationLoadPort {
    Station findOne(Station.Id stationId);

}
