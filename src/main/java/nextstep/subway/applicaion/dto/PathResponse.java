package nextstep.subway.applicaion.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import nextstep.subway.domain.Station;

@Getter
public class PathResponse {

	private final List<StationResponse> stations;

	private final long distance;

	public PathResponse(List<Station> stations, double distance) {
		this.stations = convertResponse(stations);
		this.distance = (long)distance;
	}

	private List<StationResponse> convertResponse(List<Station> stations) {
		return stations.stream()
			.map(station -> new StationResponse(station.getId(), station.getName()))
			.collect(Collectors.toUnmodifiableList());
	}
}
