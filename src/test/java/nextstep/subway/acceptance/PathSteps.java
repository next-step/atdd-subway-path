package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class PathSteps {
	public static ExtractableResponse<Response> 경로_조회_요청(Long sourceId, Long targetId) {
		return RestAssured.given().log().all()
			.when().get("/paths?source={stationId}&target={targetId}", sourceId, targetId)
			.then().log().all().extract();
	}
}
