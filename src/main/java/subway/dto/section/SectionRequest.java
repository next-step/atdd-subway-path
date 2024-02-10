package subway.dto.section;

public class SectionRequest {
	private Long downStationId;
	private Long upStationId;
	private Integer distance;

	public SectionRequest() {
	}

	public SectionRequest(Long downStationId, Long upStationId, Integer distance) {
		this.downStationId = downStationId;
		this.upStationId = upStationId;
		this.distance = distance;
	}

	public static Builder builder() {
		return new Builder();
	}

	public Long getDownStationId() {
		return downStationId;
	}

	public Long getUpStationId() {
		return upStationId;
	}

	public Integer getDistance() {
		return distance;
	}

	public static class Builder {
		private Long downStationId;
		private Long upStationId;
		private Integer distance;

		Builder() {
		}

		public Builder downStationId(Long downStationId) {
			this.downStationId = downStationId;
			return this;
		}

		public Builder upStationId(Long upStationId) {
			this.upStationId = upStationId;
			return this;
		}

		public Builder distance(Integer distance) {
			this.distance = distance;
			return this;
		}

		public SectionRequest build() {
			return new SectionRequest(downStationId, upStationId, distance);
		}
	}
}
