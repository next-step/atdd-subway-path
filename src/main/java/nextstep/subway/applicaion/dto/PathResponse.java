package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {
	private final List<StationResponse> stations;
	private int distance;

	private PathResponse(List<StationResponse> stations, int distance) {
		this.stations = stations;
		this.distance = distance;
	}

	public static PathResponse of(List<Station> stations, double distance) {
		List<StationResponse> stationResponses = stations.stream()
				.map(StationResponse::from)
				.collect(Collectors.toList());

		return new PathResponse(stationResponses, (int) distance);
	}

	public int getDistance() {
		return distance;
	}

	public List<StationResponse> getStations() {
		return stations;
	}
}
