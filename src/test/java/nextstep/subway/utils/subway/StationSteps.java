package nextstep.subway.utils.subway;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.StationRequest;
import nextstep.subway.utils.RestApiRequest;

public class StationSteps {
	private static final String STATION_API_URL = "/stations";
	private static final RestApiRequest<StationRequest> apiRequest = new RestApiRequest<>();

	public static ExtractableResponse<Response> 역_생성_요청(String name) {
		return apiRequest.post(STATION_API_URL, new StationRequest(name));
	}
}
