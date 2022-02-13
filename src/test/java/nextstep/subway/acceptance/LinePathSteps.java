package nextstep.subway.acceptance;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class LinePathSteps {
	public static ExtractableResponse<Response> 지하철_최단_경로_조회(Long sourceId, Long targetId) {
		Map<String, String> params = new HashMap<>();
		params.put("source", sourceId + "");
		params.put("target", targetId + "");
		return RestAssured
			.given().log().all()
			.queryParams(params)
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().get("/paths")
			.then().log().all().extract();
	}
}
