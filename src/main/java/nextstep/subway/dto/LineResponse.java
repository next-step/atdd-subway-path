package nextstep.subway.dto;

import nextstep.subway.entity.Line;

import java.util.List;

public class LineResponse {
	private Long id;

	private String name;

	private String color;

	private List<StationResponse> staions;

	public LineResponse() {
	}

	public LineResponse(Line line, List<StationResponse> stations) {
		this.id = line.getId();
		this.name = line.getName();
		this.color = line.getColor();
		this.staions = stations;
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

	public List<StationResponse> getStaions() {
		return staions;
	}
}
