package nextstep.subway.utils.resthelper;

import java.util.List;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.api.interfaces.dto.response.LineResponse;

/**
 * @author : Rene Choi
 * @since : 2024/01/31
 */
public class ExtractableResponseParser {

	public static long parseId(ExtractableResponse<Response> createResponse) {
		return createResponse.jsonPath().getLong("id");
	}

	public static List<Long> parseStationIds(ExtractableResponse<Response> response) {
		return response.jsonPath().getList("stations.id", Long.class);
	}

	public static List<LineResponse> parseLines(ExtractableResponse<Response> response) {
		return response.jsonPath().getList("", LineResponse.class);
	}

	public static List<String> parseLineNames(ExtractableResponse<Response> response) {
		return response.jsonPath().getList("name", String.class);
	}

	public static List<String> parseSubwayNames(ExtractableResponse<Response> response) {
		return response.jsonPath().getList("name", String.class);
	}

}
