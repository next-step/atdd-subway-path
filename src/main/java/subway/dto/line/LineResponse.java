package subway.dto.line;

import static java.util.stream.Collectors.*;

import java.util.List;

import subway.dto.station.StationResponse;
import subway.line.Line;

public class LineResponse {
	private final Long id;
	private final String name;
	private final String color;
	private final List<StationResponse> stations;

	private LineResponse(Long id, String name, String color, List<StationResponse> station) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.stations = station;
	}

	public static LineResponse of(Line line) {
		List<StationResponse> stationDto = line.getSortedStations()
			.stream()
			.map(StationResponse::of)
			.collect(toList());
		return new LineResponse(line.getId(), line.getName(), line.getColor(), stationDto);
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}

	public List<StationResponse> getStations() {
		return stations;
	}
}
