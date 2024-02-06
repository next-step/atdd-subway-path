package nextstep.subway.fixture;

import nextstep.subway.api.interfaces.dto.request.SectionCreateRequest;

/**
 * @author : Rene Choi
 * @since : 2024/01/31
 */
public class SectionFixtureCreator {

	/**
	 * Station의 파라미터 순서에 유의하세요.
	 * @param downStationId
	 * @param upStationId
	 * @param distance
	 * @return
	 */
	public static SectionCreateRequest createSectionCreateRequestWithUpAndDownAndDistance(Long upStationId, Long downStationId, Long distance) {
		return SectionCreateRequest.builder()
			.upStationId(upStationId)
			.downStationId(downStationId)
			.distance(distance)
			.build();
	}
}
