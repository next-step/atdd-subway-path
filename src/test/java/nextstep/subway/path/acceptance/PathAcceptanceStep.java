package nextstep.subway.path.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class PathAcceptanceStep {

    public static ExtractableResponse<Response> 출발역에서_도착역까지_최단경로를_조회한다(Long sourceId, Long targetId, String type) {
        return RestAssured.given().log().all()
            .queryParam("source", sourceId)
            .queryParam("target", targetId)
            .queryParam("type", type)
            .get("/paths")
            .then().log().all().extract();
    }
}
