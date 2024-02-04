package nextstep.subway.utils.subway;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.utils.RestApiRequest;

import java.util.Map;

public class SectionSteps {
	private static final String SECTION_API_URL = "/lines/{id}/sections";
	private static final RestApiRequest<SectionRequest> apiRequest = new RestApiRequest<>();

	public static ExtractableResponse<Response> 구간_생성_요청(Long downStationId, Long upStationId, int distance, Long lineId) {
		return apiRequest.post(SECTION_API_URL, new SectionRequest(downStationId, upStationId, distance), lineId);
	}

	public static ExtractableResponse<Response> 구간_조회_요청(Long lineId) {
		return apiRequest.get(SECTION_API_URL, lineId);
	}

	public static ExtractableResponse<Response> 구간_삭제_요청(Map<String, Object> queryParams, Long lineId) {
		return apiRequest.delete(SECTION_API_URL, queryParams, lineId);
	}
}
