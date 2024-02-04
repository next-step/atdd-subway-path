package nextstep.subway.station.service;

import nextstep.subway.station.service.dto.StationRequest;
import nextstep.subway.station.service.dto.StationResponse;

import java.util.List;

public interface StationService {
    StationResponse saveStation(StationRequest stationRequest);

    List<StationResponse> findAllStations();

    void deleteStationById(Long id);
}
