package nextstep.subway.steps;

import java.util.HashMap;
import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class PathSteps {

	public static ExtractableResponse<Response> searchPath(Long source, Long target) {
		Map<String, Long> params = new HashMap<>();
		params.put("source", source);
		params.put("target", target);

		ExtractableResponse<Response> searchResponse = RestAssured
			.given().log().all()
			.params(params)
			.when().get("/paths")
			.then().log().all()
			.extract();
		return searchResponse;
	}
}
