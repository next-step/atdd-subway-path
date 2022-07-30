package nextstep.subway.applicaion.dto;

import java.util.List;

public class PathResponse {
	private final List<StationResponse> stations;
	private final int distance;

	private PathResponse(List<StationResponse> stations, int distance) {
		this.stations = stations;
		this.distance = distance;
	}

	public static PathResponse of(List<StationResponse> stationResponseList, int distance) {
		return new PathResponse(stationResponseList, distance);
	}

	public List<StationResponse> getStations() {
		return this.stations;
	}

	public int getDistance() {
		return this.distance;
	}
}
