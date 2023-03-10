package nextstep.subway.applicaion.dto;

import java.util.List;

public class PathResponse {
	private List<StationResponse> stations;
	private int distance;

	protected PathResponse() {
	}

	public PathResponse(List<StationResponse> stations, int distance) {
		this.stations = stations;
		this.distance = distance;
	}

	public List<StationResponse> getStations() {
		return stations;
	}

	public void setStations(List<StationResponse> stations) {
		this.stations = stations;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}
}
