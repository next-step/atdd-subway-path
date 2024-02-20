package nextstep.subway.utils.subway;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.PathResponse;
import nextstep.subway.utils.RestApiRequest;

import java.util.Map;

public class PathSteps {
	private static final String PATH_API_URL = "/paths";

	private static final RestApiRequest<PathResponse> apiRequest = new RestApiRequest<>();

	public static ExtractableResponse<Response> 최단_경로_조회_요청(Long source, Long target) {
		return apiRequest.get(PATH_API_URL, Map.of("source", source, "target", target));
	}
}
