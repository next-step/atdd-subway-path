package subway.fixture.line;

import subway.dto.line.LineCreateRequest;
import subway.dto.line.LineUpdateRequest;

public class LineRequestFixture {
	public static LineCreateRequest.Builder lineCreateRequest() {
		return LineCreateRequest.builder()
			.name("TEST NAME")
			.color("TEST COLOR")
			.upStationId(1L)
			.downStationId(2L)
			.distance(5);
	}

	public static LineCreateRequest.Builder lineCreateRequest(Long upStationId, Long downStationId) {
		return LineCreateRequest.builder()
			.name("TEST NAME")
			.color("TEST COLOR")
			.upStationId(upStationId)
			.downStationId(downStationId)
			.distance(10);
	}

	public static LineUpdateRequest.Builder lineUpdateRequest() {
		return LineUpdateRequest.builder()
			.name("노선 이름")
			.color("노선 색깔");
	}
}
