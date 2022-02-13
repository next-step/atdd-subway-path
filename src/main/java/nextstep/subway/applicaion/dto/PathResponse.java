package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Station;

import java.util.List;

public class PathResponse {
	private List<Station> stations;
	private int distance;

	public List<Station> getStations() {
		return stations;
	}

	public int getDistance() {
		return distance;
	}
}
