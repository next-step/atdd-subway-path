package nextstep.subway.applicaion.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LineRequest {
	private String name;
	private String color;

	private Long upStationId;

	private Long downStationId;

	private int distance;

	public LineRequest(
		String name,
		String color,
		Long upStationId,
		Long downStationId,
		int distance) {
		this.name = name;
		this.color = color;
		this.upStationId = upStationId;
		this.downStationId = downStationId;
		this.distance = distance;
	}

	public boolean hasUpAndDownStation() {
		return this.upStationId != null && this.downStationId != null;
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}

	public Long getUpStationId() {
		return upStationId;
	}

	public Long getDownStationId() {
		return downStationId;
	}

	public int getDistance() {
		return distance;
	}
}
