package nextstep.subway.dto;

public class LineRequest {
	private String name;

	private String color;

	private Long startStationId;

	private Long endStationId;

	private int distance;

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}

	public Long getStartStationId() {
		return startStationId;
	}

	public Long getEndStationId() {
		return endStationId;
	}

	public int getDistance() {
		return distance;
	}

	public LineRequest() {
	}

	public LineRequest(String name, String color, Long startStationId, Long endStationId, int distance) {
		this.name = name;
		this.color = color;
		this.startStationId = startStationId;
		this.endStationId = endStationId;
		this.distance = distance;
	}

	public LineRequest(String name, String color) {
		this.name = name;
		this.color = color;
	}
}
