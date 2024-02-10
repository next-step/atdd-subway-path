package subway.dto.line;

import subway.line.Line;

public class LineCreateRequest {
	private final String name;

	private final String color;

	private final Long upStationId;

	private final Long downStationId;

	private final Integer distance;

	private LineCreateRequest(String name, String color, Long upStationId, Long downStationId, Integer distance) {
		this.name = name;
		this.color = color;
		this.upStationId = upStationId;
		this.downStationId = downStationId;
		this.distance = distance;
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

	public Integer getDistance() {
		return distance;
	}

	public Line toEntity() {
		return new Line(name, color);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private String name;
		private String color;
		private Long upStationId;
		private Long downStationId;
		private Integer distance;

		Builder() {
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder color(String color) {
			this.color = color;
			return this;
		}

		public Builder upStationId(Long upStationId) {
			this.upStationId = upStationId;
			return this;
		}

		public Builder downStationId(Long downStationId) {
			this.downStationId = downStationId;
			return this;
		}

		public Builder distance(Integer distance) {
			this.distance = distance;
			return this;
		}

		public LineCreateRequest build() {
			return new LineCreateRequest(name, color, upStationId, downStationId, distance);
		}
	}
}
