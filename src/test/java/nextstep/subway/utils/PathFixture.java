package nextstep.subway.utils;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class PathFixture {

	public static ExtractableResponse<Response> 지하철_경로_조회(Long source, Long target) {
		return RestAssured.given().log().all()
			.param("source", source)
			.param("target", target)
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.when().get("/paths")
			.then().log().all()
			.extract();
	}
}
