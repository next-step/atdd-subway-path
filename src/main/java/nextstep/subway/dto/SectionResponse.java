package nextstep.subway.dto;

import nextstep.subway.entity.Section;

public class SectionResponse {
	private Long id;

	private Long downStationId;

	private Long upStationId;

	private int distance;

	public SectionResponse() {
	}

	public SectionResponse(Section section) {
		this.id = section.getId();
		this.downStationId = section.getDownStationId();
		this.upStationId = section.getUpStationId();
		this.distance = section.getDistance();
	}

	public Long getId() {
		return id;
	}

	public Long getDownStationId() {
		return downStationId;
	}

	public Long getUpStationId() {
		return upStationId;
	}

	public int getDistance() {
		return distance;
	}
}
