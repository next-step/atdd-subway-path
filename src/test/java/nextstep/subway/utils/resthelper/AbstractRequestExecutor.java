package nextstep.subway.utils.resthelper;

import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * @author : Rene Choi
 * @since : 2024/01/27
 */

public abstract class AbstractRequestExecutor  {

	protected static ExtractableResponse<Response> doPost(RequestSpecification requestSpecification, String urlPath) {
		return requestSpecification
			.when().post(urlPath)
			.then().log().all()
			.extract();
	}

	protected static <T> ExtractableResponse<Response> doPost(RequestSpecification requestSpecification, String urlPath, T requestBody) {
		return requestSpecification
			.body(requestBody)
			.when().post(urlPath)
			.then().log().all()
			.extract();
	}


	protected static ExtractableResponse<Response> doGet(RequestSpecification requestSpecification, String urlPath) {
		return requestSpecification
			.when().get(urlPath)
			.then().log().all()
			.extract();
	}

	protected static ExtractableResponse<Response> doGetWithOk(RequestSpecification requestSpecification, String urlPath) {
		return requestSpecification
			.when().get(urlPath)
			.then().log().all()
			.statusCode(HttpStatus.OK.value())
			.extract();
	}

	protected static ExtractableResponse<Response> doPut(RequestSpecification requestSpecification, String urlPath) {
		return requestSpecification
			.when().put(urlPath)
			.then().log().all()
			.extract();
	}

	protected static <T> ExtractableResponse<Response> doPut(RequestSpecification requestSpecification, String urlPath, T requestBody) {
		return requestSpecification
			.body(requestBody)
			.when().put(urlPath)
			.then().log().all()
			.extract();
	}

	protected static ExtractableResponse<Response> doDelete(RequestSpecification requestSpecification, String urlPath) {
		return requestSpecification
			.when().delete(urlPath)
			.then().log().all()
			.extract();
	}

}

