package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

public class PathSteps {
    public static ExtractableResponse<Response> 지하철_최단경로_조회(final Long sourceId, final Long targetId) {
        return RestAssured
                    .given()
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                        .get("/paths?source={sourceId}&target={targetId}", sourceId, targetId)
                    .then()
                    .extract();
    }
}
