package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

public class PathSteps {
    public static ExtractableResponse<Response> 경로_조회_요청(Long sourceId, Long targetId) {
        return RestAssured.given().log().all()
            .queryParam("source", sourceId)
            .queryParam("target", targetId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/paths")
            .then().log().all()
            .extract();
    }
}