package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

public class PathFinderSteps {

	public static ExtractableResponse<Response> 최단경로_조회(Long source, Long target) {
		return RestAssured.given().log().all()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("source", source)
				.param("target", target)
				.when().get("paths")
				.then().log().all().extract();
	}
}
