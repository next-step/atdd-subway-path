package nextstep.subway.map.acceptance;

import static org.assertj.core.api.Assertions.*;

import org.springframework.http.HttpStatus;
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

	public static void 지하철_노선도가_응답된다(ExtractableResponse<Response> response) {
		int statusCode = response.statusCode();
		assertThat(statusCode).isEqualTo(HttpStatus.OK.value());
	}

	public static void 지하철_노선도에_eTag가_명시된다(ExtractableResponse<Response> response) {
		assertThat(response.headers().toString()).contains("eTag");
	}

	public static void 지하철_노선도_응답시_캐시가_적용된다(ExtractableResponse<Response> response) {
		int statusCode = response.statusCode();
		assertThat(statusCode).isEqualTo(HttpStatus.NOT_MODIFIED.value());
	}
}
