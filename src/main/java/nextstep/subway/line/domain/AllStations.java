package nextstep.subway.line.domain;

import java.util.Map;

import nextstep.subway.station.domain.Station;

public class AllStations {

	private final Map<Long, Station> stationsMap;

	public AllStations(Map<Long, Station> stationsMap) {
		this.stationsMap = stationsMap;
	}

	public Map<Long, Station> getStationsMap() {
		return stationsMap;
	}

	public Station findByStationId(Long stationId) {
		return stationsMap.get(stationId);
	}
}
