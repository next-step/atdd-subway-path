package nextstep.subway.dto;

import java.util.List;

import nextstep.subway.domain.Path;

public class PathResponse {

	private List<StationResponse> stations;
	private int distance;

	public PathResponse() {
	}

	public PathResponse(List<StationResponse> stations, int distance) {
		this.stations = stations;
		this.distance = distance;
	}

	public static PathResponse from(Path path) {
		return new PathResponse(StationResponse.from(path.getStations()), path.getTotalDistance());
	}

	public List<StationResponse> getStations() {
		return stations;
	}

	public int getDistance() {
		return distance;
	}
}
