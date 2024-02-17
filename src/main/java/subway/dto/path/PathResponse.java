package subway.dto.path;

import java.util.List;

import subway.dto.station.StationResponse;

public class PathResponse {
	private final List<StationResponse> stations;
	private final Integer distance;

	public PathResponse(List<StationResponse> stations, Integer distance) {
		this.stations = stations;
		this.distance = distance;
	}
}
