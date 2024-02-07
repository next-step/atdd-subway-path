package nextstep.subway.fixture;

import nextstep.subway.api.interfaces.dto.request.LineCreateRequest;
import nextstep.subway.api.interfaces.dto.request.LineUpdateRequest;

/**
 * @author : Rene Choi
 * @since : 2024/01/27
 */
public class LineFixtureCreator {

	public static LineCreateRequest createStationLineCreateDefaultRequest() {
		return LineCreateRequest.builder()
			.name("신분당선")
			.color("bg-red-600")
			.upStationId(1L)
			.downStationId(2L)
			.distance(10L)
			.build();
	}

	public static LineCreateRequest createLineCreateRequest(String name) {
		return LineCreateRequest.builder()
			.name(name)
			.color("bg-red-600")
			.upStationId(1L)
			.downStationId(2L)
			.distance(10L)
			.build();
	}

	public static LineCreateRequest createLineCreateRequest(String name, Long upStationId, Long downStationId) {
		return LineCreateRequest.builder()
			.name(name)
			.color("bg-red-600")
			.upStationId(upStationId)
			.downStationId(downStationId)
			.distance(10L)
			.build();
	}

	public static LineCreateRequest createLineCreateRequest(String name, Long upStationId, Long downStationId, Long distance) {
		return LineCreateRequest.builder()
			.name(name)
			.color("bg-red-600")
			.upStationId(upStationId)
			.downStationId(downStationId)
			.distance(distance)
			.build();
	}


	public static LineUpdateRequest createStationLineUpdateDefaultRequest(String name, String color) {
		return LineUpdateRequest.builder()
			.name(name)
			.color(color)
			.build();
	}



}
