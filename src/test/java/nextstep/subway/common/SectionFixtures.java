package nextstep.subway.common;

import nextstep.subway.applicaion.dto.SectionRequest;

public class SectionFixtures {

	public static SectionRequest 구간_추가_요청(Long upStationId, Long downStationId, int distance) {
		return new SectionRequest(upStationId, downStationId, distance);
	}
}
