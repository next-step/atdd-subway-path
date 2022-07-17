package nextstep.subway.applicaion.dto;

import java.util.List;

import nextstep.subway.domain.Line;

public class LineResponse {
	private Long id;
	private String name;
	private String color;
	private List<StationResponse> stations;

	public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.stations = stations;
	}

	public static LineResponse of(Line line, List<StationResponse> stationResponseList) {
		return new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponseList);
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

