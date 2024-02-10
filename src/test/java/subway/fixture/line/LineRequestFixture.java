package subway.fixture.line;

import subway.dto.line.LineCreateRequest;
import subway.dto.line.LineUpdateRequest;

public class LineRequestFixture {
	public static LineCreateRequest.Builder createBuilder() {
		return LineCreateRequest.builder()
			.name("TEST NAME")
			.color("TEST COLOR")
			.upStationId(1L)
			.downStationId(2L)
			.distance(1);
	}

	public static LineUpdateRequest.Builder updateBuilder() {
		return LineUpdateRequest.builder()
			.name("노선 이름")
			.color("노선 색깔");
	}
}
