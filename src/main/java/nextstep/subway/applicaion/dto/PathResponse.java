package nextstep.subway.applicaion.dto;

import java.util.List;

public class PathResponse {
	private long distance;
	private List<StationResponse> stations;

	public PathResponse(long distance, List<StationResponse> stationResponseList) {
		this.distance = distance;
		this.stations = stationResponseList;
	}

	public long getDistance() {
		return distance;
	}

	public List<StationResponse> getStations() {
		return stations;
	}
}
