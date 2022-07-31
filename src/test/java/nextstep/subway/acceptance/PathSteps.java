package nextstep.subway.acceptance;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class PathSteps {
	public static ExtractableResponse<Response> 경로조회_요청(Long source, Long target) {
		return RestAssured.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get("/paths?source={source}&target={target}", source, target)
			.then().log().all().extract();
	}
}
