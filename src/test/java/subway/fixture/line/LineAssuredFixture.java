package subway.fixture.line;

import static subway.utils.enums.Location.*;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import subway.dto.line.LineCreateRequest;
import subway.dto.line.LineResponse;
import subway.fixture.station.StationAssuredFixture;
import subway.utils.rest.Rest;

public class LineAssuredFixture {
	private final String upStationName;
	private final String downStationName;

	private LineAssuredFixture(String upStationName, String downStationName) {
		this.upStationName = upStationName;
		this.downStationName = downStationName;
	}

	public static Builder builder() {
		return new Builder()
			.upStationName("강남역")
			.downStationName("양재역");
	}

	private ExtractableResponse<Response> action(LineCreateRequest request) {
		return Rest.builder()
			.uri(LINES.path())
			.body(request)
			.post();
	}

	public ExtractableResponse<Response> create() {
		Long upStationId = generateStationId(upStationName);
		Long downStationId = generateStationId(downStationName);

		LineCreateRequest lineCreateRequest = LineRequestFixture.createBuilder()
			.upStationId(upStationId)
			.downStationId(downStationId)
			.build();

		return action(lineCreateRequest);
	}

	private Long generateStationId(String stationName) {
		return StationAssuredFixture.builder()
			.stationName(stationName)
			.build()
			.create()
			.jsonPath()
			.getLong("id");
	}

	public LineResponse actionReturnLineResponse() {
		return create().as(LineResponse.class);
	}

	public static class Builder {
		private String upStationName;
		private String downStationName;

		Builder() {
		}

		public Builder upStationName(String upStationName) {
			this.upStationName = upStationName;
			return this;
		}

		public Builder downStationName(String downStationName) {
			this.downStationName = downStationName;
			return this;
		}

		public LineAssuredFixture build() {
			return new LineAssuredFixture(upStationName, downStationName);
		}
	}
}
