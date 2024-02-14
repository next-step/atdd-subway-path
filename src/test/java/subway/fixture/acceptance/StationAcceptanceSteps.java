package subway.fixture.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import subway.dto.line.LineResponse;
import subway.dto.station.StationRequest;
import subway.utils.enums.Location;
import subway.utils.rest.Rest;

public class StationAcceptanceSteps {
	public static ExtractableResponse<Response> 정류장_생성(String name) {
		StationRequest stationRequest = new StationRequest(name);
		return Rest.builder()
			.uri(Location.STATIONS.path())
			.body(stationRequest)
			.post();
	}

	public static Long 정류장_생성_ID_반환(String name) {
		return 정류장_생성(name).as(LineResponse.class).getId();
	}
}
