package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.Map;

public class RestApiRequest<T> {
	public RestApiRequest() {
	}

	public ExtractableResponse<Response> get(String path, Object... pathParams) {
		return RestAssured.given().log().all()
				.when()
				.get(path, pathParams)
				.then().log().all()
				.extract();
	}

	public ExtractableResponse<Response> post(String path, T body, Object... pathParams) {
		return RestAssured.given().log().all()
				.body(body)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when()
				.post(path, pathParams)
				.then().log().all()
				.extract();
	}

	public ExtractableResponse<Response> put(String path, T body, Object... pathParams) {
		return RestAssured.given().log().all()
				.body(body)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when()
				.put(path, pathParams)
				.then().log().all()
				.extract();
	}

	public ExtractableResponse<Response> delete(String path, Object... pathParams) {
		return RestAssured.given().log().all()
				.when()
				.delete(path, pathParams)
				.then().log().all()
				.extract();
	}

	public ExtractableResponse<Response> delete(String path, Map<String, Object> queryParams, Object... pathParams) {
		return RestAssured.given().log().all()
				.queryParams(queryParams)
				.when()
				.delete(path, pathParams)
				.then().log().all()
				.extract();
	}
}
