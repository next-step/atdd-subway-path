package nextstep.subway.path.dto;

import java.util.List;

import nextstep.subway.station.dto.StationResponse;

public class PathResponse {
	private List<StationResponse> stations;
	private int distance;

	private PathResponse(List<StationResponse> stations, int distance) {
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
