package nextstep.subway.utils.subway;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.utils.RestApiRequest;

public class LineSteps {
	private static final String LINE_API_URL = "/lines";
	private static final String LINE_API_WITH_ID_URL = "/lines/{id}";
	private static final RestApiRequest<LineRequest> apiRequest = new RestApiRequest<>();

	public static ExtractableResponse<Response> 노선_생성_요청(String name, String color, Long startStationId, Long endStationId, int distance) {
		return apiRequest.post(LINE_API_URL, new LineRequest(name, color, startStationId, endStationId, distance));
	}

	public static ExtractableResponse<Response> 노선_전체_조회_요청() {
		return apiRequest.get(LINE_API_URL);
	}

	public static ExtractableResponse<Response> 노선_단건_조회_요청(Long id) {
		return apiRequest.get(LINE_API_WITH_ID_URL, id);
	}

	public static ExtractableResponse<Response> 노선_수정_요청(String name, String color, Long id) {
		return apiRequest.put(LINE_API_WITH_ID_URL, new LineRequest(name, color), id);
	}

	public static ExtractableResponse<Response> 노선_삭제_요청(Long id) {
		return apiRequest.delete(LINE_API_WITH_ID_URL, id);
	}
}
