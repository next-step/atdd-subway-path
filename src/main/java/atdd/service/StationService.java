package atdd.service;

import atdd.dto.Station;

import java.util.List;

public interface StationService {

    Station createStation(Station station);

    List<Station> getStation();

    Station detailStation(String stationName);

    void deleteStation(String stationName);

}
