package nextstep.subway.utils.resthelper;

import static io.restassured.RestAssured.*;
import static org.springframework.http.MediaType.*;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * @author : Rene Choi
 * @since : 2024/01/31
 */
public class PathRequestExecutor extends AbstractRequestExecutor {

	private static final String LINE_URL_PATH = "/paths";

	private static RequestSpecification getRequestSpecification() {
		return given().log().all().contentType(APPLICATION_JSON_VALUE);
	}

	public static ExtractableResponse<Response> executeFindPathRequest(Long source, Long target) {
		return doGet(getRequestSpecification().queryParam("source", source).queryParam("target", target), LINE_URL_PATH);
	}

}