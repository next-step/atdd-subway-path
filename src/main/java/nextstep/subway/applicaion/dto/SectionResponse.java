package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Section;

import java.util.List;
import java.util.stream.Collectors;

public class SectionResponse {
	private Long upStationId;
	private Long downStationId;
	private int distance;

	public Long getUpStationId() {
		return upStationId;
	}

	public Long getDownStationId() {
		return downStationId;
	}

	public int getDistance() {
		return distance;
	}

	public SectionResponse(Long upStationId, Long downStationId, int distance) {
		this.upStationId = upStationId;
		this.downStationId = downStationId;
		this.distance = distance;
	}

	public static List<SectionResponse> of(List<Section> sections) {
		List<SectionResponse> collect = sections.stream()
				.map(SectionResponse::of)
				.collect(Collectors.toList());
		return collect;
	}

	public static SectionResponse of(Section section) {
		return new SectionResponse(section.getUpStation().getId(), section.getDownStation().getId(), section.getDistance());
	}
}
