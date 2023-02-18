package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {
	private List<StationResponse> stations;
	private int distance;

	public PathResponse(int distance, List<StationResponse> stations) {
		this.distance = distance;
		this.stations = stations;
	}

	public static PathResponse of(int distance, List<Station> stations) {
		return new PathResponse(
				distance,
				stations.stream()
						.map(StationResponse::of)
						.collect(Collectors.toList()));
	}

	public List<StationResponse> getStations() {
		return stations;
	}
	
	public List<Long> getStationIds() {
		return stations.stream()
				.map(stationResponse -> stationResponse.getId())
				.collect(Collectors.toList());
	}

	public int getDistance() {
		return distance;
	}
}
