package nextstep.subway.acceptance;

import static io.restassured.RestAssured.*;

import org.springframework.http.MediaType;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class PathSteps {

	public static ExtractableResponse<Response> 최단경로_조회_요청(Long source, Long target) {
		return given()
			.log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.param("source", source)
			.param("target", target)
			.when()
			.get("/paths")
			.then()
			.log().all()
			.extract();
	}
}
