package nextstep.subway.station.service;

import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.repository.Station;

public interface StationSavable {
    Station saveStation(StationRequest stationRequest);
}
