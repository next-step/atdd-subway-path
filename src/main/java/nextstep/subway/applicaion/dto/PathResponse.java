package nextstep.subway.applicaion.dto;

import java.util.List;

public class PathResponse {
	List<StationResponse> stations;
	int distance;

	public PathResponse(List<StationResponse> stations, int distance) {
		this.stations = stations;
		this.distance = distance;
	}

	public static PathResponse of(List<StationResponse> stations, int distance) {
		return new PathResponse(stations, distance);
	}

	public List<StationResponse> getStations() {
		return stations;
	}

	public int getDistance() {
		return distance;
	}
}
