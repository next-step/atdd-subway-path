package subway.fixture;

import static subway.utils.enums.Location.*;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import subway.utils.rest.Rest;
import subway.dto.station.StationRequest;

public class StationFixture {
	private final String stationName;

	private StationFixture(String stationName) {
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

		public StationFixture build() {
			return new StationFixture(stationName);
		}
	}
}
