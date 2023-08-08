package subway.application.command.out;

import subway.domain.Station;

import java.util.Optional;

public interface StationLoadPort {
    Optional<Station> findOne(Station.Id stationId);

}
