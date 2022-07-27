package nextstep.subway.applicaion.dto;

import java.util.List;

import nextstep.subway.domain.Station;

public class PathResponse {
	private long distance;
	private List<Station> stationList;

	public long getDistance() {
		return distance;
	}

	public List<Station> getStationList() {
		return stationList;
	}
}
