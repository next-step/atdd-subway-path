package nextstep.subway.utils.resthelper;

import static io.restassured.RestAssured.*;
import static org.springframework.http.MediaType.*;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import nextstep.subway.api.interfaces.dto.request.SectionCreateRequest;

/**
 * @author : Rene Choi
 * @since : 2024/01/31
 */
public class SectionRequestExecutor extends AbstractRequestExecutor {

	private static final String LINE_URL_PATH = "/lines";
	private static final String SECTION_URL_PATH = "/sections";

	private static RequestSpecification getRequestSpecification() {
		return given().log().all().contentType(APPLICATION_JSON_VALUE);
	}

	public static ExtractableResponse<Response> executeCreateSectionRequest(Long lineId, SectionCreateRequest createRequest) {
		return doPost(getRequestSpecification(), LINE_URL_PATH + "/" + lineId + SECTION_URL_PATH, createRequest);
	}

	public static ExtractableResponse<Response> executeDeleteSectionRequest(Long lineId, Long downStationId) {
		return doDelete(getRequestSpecification(), LINE_URL_PATH + "/" + lineId + SECTION_URL_PATH + "/" + downStationId);
	}
}