package nextstep.subway.utils.resthelper;

import static io.restassured.RestAssured.*;
import static org.springframework.http.MediaType.*;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import nextstep.subway.api.interfaces.dto.request.LineCreateRequest;
import nextstep.subway.api.interfaces.dto.request.LineUpdateRequest;

/**
 * @author : Rene Choi
 * @since : 2024/01/26
 */
public class LineRequestExecutor extends AbstractRequestExecutor {
	private static final String URL_PATH = "/lines";

	private static RequestSpecification getRequestSpecification() {
		return given().log().all().contentType(APPLICATION_JSON_VALUE);
	}

	public static ExtractableResponse<Response> executeCreateLineRequest(LineCreateRequest createRequest) {
		return doPost(getRequestSpecification(), URL_PATH, createRequest);
	}

	public static ExtractableResponse<Response> executeGetAllStationLineRequest() {
		return doGet(getRequestSpecification(), URL_PATH);
	}

	public static ExtractableResponse<Response> executeGetSpecificStationLineRequest(Long id) {
		return doGet(getRequestSpecification(), URL_PATH + "/" + id);
	}

	public static ExtractableResponse<Response> executeGetSpecificStationLineRequestWithOk(Long id) {
		return doGetWithOk(getRequestSpecification(), URL_PATH + "/" + id);
	}

	public static ExtractableResponse<Response> executeUpdateLineRequest(Long id, LineUpdateRequest updateRequest) {
		return doPut(getRequestSpecification(), URL_PATH + "/" + id, updateRequest);
	}

	public static ExtractableResponse<Response> executeDeleteLineRequest(Long id) {
		return doDelete(getRequestSpecification(), URL_PATH + "/" + id);
	}
}
