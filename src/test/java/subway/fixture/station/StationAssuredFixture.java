package subway.fixture.station;

import static subway.utils.enums.Location.*;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import subway.dto.station.StationRequest;
import subway.utils.rest.Rest;

public class StationAssuredFixture {
	private final String stationName;

	private StationAssuredFixture(String stationName) {
		this.stationName = stationName;
	}

	public static Builder builder() {
		return new Builder().stationName("강남역");
	}

	public ExtractableResponse<Response> create() {
		StationRequest request = new StationRequest(stationName);

		return Rest.builder()
			.uri(STATIONS.path())
			.body(request)
			.post();
	}

	public static class Builder {
		private String stationName;

		Builder() {
		}

		public Builder stationName(String stationName) {
			this.stationName = stationName;
			return this;
		}

		public StationAssuredFixture build() {
			return new StationAssuredFixture(stationName);
		}
	}
}
