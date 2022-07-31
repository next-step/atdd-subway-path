package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class PathSteps {
    public static ExtractableResponse<Response> 최단_거리_조회_요청_성공(long sourceId, long targetId) {
        return getResponse(sourceId, targetId).statusCode(HttpStatus.OK.value()).extract();
    }

    public static ExtractableResponse<Response> 최단_거리_조회_요청(long sourceId, long targetId) {
        return getResponse(sourceId, targetId).extract();
    }

    private static ValidatableResponse getResponse(long sourceId, long targetId) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .queryParam("sourceId", sourceId)
            .queryParam("targetId", targetId)
            .when().get("/paths")
            .then().log().all();
    }
}
