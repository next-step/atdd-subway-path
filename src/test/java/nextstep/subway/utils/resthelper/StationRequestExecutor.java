package nextstep.subway.utils.resthelper;

import static io.restassured.RestAssured.*;
import static org.springframework.http.MediaType.*;

import java.util.HashMap;
import java.util.Map;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * @author : Rene Choi
 * @since : 2024/01/26
 */
public class StationRequestExecutor extends AbstractRequestExecutor {

	private static final String URL_PATH = "/stations";

	private static RequestSpecification getRequestSpecification() {
		return given().log().all().contentType(APPLICATION_JSON_VALUE);
	}

	public static ExtractableResponse<Response> executeCreateStationRequest(String name) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		return doPost(getRequestSpecification(), URL_PATH, params);
	}

	public static ExtractableResponse<Response> executeDeleteStationRequest(Long id) {
		return doDelete(getRequestSpecification(), URL_PATH + "/" + id);
	}

	public static ExtractableResponse<Response> executeGetStationRequest() {
		return doGet(getRequestSpecification(), URL_PATH);
	}
}
