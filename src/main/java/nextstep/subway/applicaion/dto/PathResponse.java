package nextstep.subway.applicaion.dto;

import java.util.List;

public class PathResponse {
	List<StationResponse> stations;
	int distance;

	public PathResponse(List<StationResponse> stations, int distance) {
		this.stations = stations;
		this.distance = distance;
	}

	public List<StationResponse> getStations() {
		return stations;
	}

	public int getDistance() {
		return distance;
	}
}
