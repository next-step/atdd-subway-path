package nextstep.subway.applicaion.dto;

import java.util.List;

import nextstep.subway.domain.Station;

public class PathResponse {
	private List<Station> stations;
	private int distance;

	public PathResponse() {
	}

	public List<Station> getStations() {
		return stations;
	}

	public void setStations(List<Station> stations) {
		this.stations = stations;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}
}
