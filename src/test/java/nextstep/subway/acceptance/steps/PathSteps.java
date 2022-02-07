package nextstep.subway.acceptance.steps;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

public class PathSteps {

    private static final String BASE_PATH = "/paths/";
    private static final String SOURCE = "source";
    private static final String TARGET = "target";

    public static ExtractableResponse<Response> 지하철_출발역과_도착역간의_최단_경로_조회를_요청한다(final Long sourceId, final Long targetId) {
        return RestAssured.given().log().all()
                .accept(ContentType.JSON)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .queryParam(SOURCE, sourceId)
                .queryParam(TARGET, targetId)
                .get(String.format(BASE_PATH))
                .then().log().all()
                .extract();
    }
}
