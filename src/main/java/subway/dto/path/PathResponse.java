package subway.dto.path;

import java.util.List;
import java.util.stream.Collectors;

import subway.dto.station.StationResponse;
import subway.station.Station;

public class PathResponse {
	private final List<StationResponse> stations;
	private final Integer distance;

	public PathResponse(List<Station> stations, Integer distance) {
		this.stations = stations.stream().map(StationResponse::of).collect(Collectors.toList());
		this.distance = distance;
	}

	public List<StationResponse> getStations() {
		return stations;
	}

	public Integer getDistance() {
		return distance;
	}
}
