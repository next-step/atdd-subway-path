package nextstep.subway.applicaion.dto;

import java.util.List;

public class PathResponse {
	private final List<StationResponse> stationResponseList;
	private final int distance;

	private PathResponse(List<StationResponse> stationResponseList, int distance) {
		this.stationResponseList = stationResponseList;
		this.distance = distance;
	}

	public static PathResponse of(List<StationResponse> stationResponseList, int distance) {
		return new PathResponse(stationResponseList, distance);
	}

	public List<StationResponse> getStationResponseList() {
		return this.stationResponseList;
	}

	public int getDistance() {
		return this.distance;
	}
}
