package subway.fixture;

import subway.dto.line.LineCreateRequest;

public class LineRequestFixture {
	public static LineCreateRequest.Builder builder() {
		return LineCreateRequest.builder()
			.name("TEST NAME")
			.color("TEST COLOR")
			.upStationId(1L)
			.downStationId(2L)
			.distance(1);
	}
}
