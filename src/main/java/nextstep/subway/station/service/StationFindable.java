package nextstep.subway.station.service;

import nextstep.subway.station.repository.Station;

import java.util.List;

public interface StationFindable {
    Station findStationById(Long stationId);
    List<Station> findAllStations();
}
