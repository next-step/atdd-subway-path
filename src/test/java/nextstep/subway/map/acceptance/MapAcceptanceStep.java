package nextstep.subway.map.acceptance;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class MapAcceptanceStep {

	public static ExtractableResponse<Response> 지하철_노선도를_캐시로_요청한다() {
		return RestAssured.given().log().all()
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get("/maps")
			.then()
			.log().all()
			.extract();
	}

	public static ExtractableResponse<Response> 지하철_노선도를_캐시로_요청한다(String eTag) {
		return RestAssured.given().log().all()
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.header("If-None-Match", eTag)
			.get("/maps")
			.then()
			.log().all()
			.extract();
	}
}
