package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class PathSteps {
    public static ExtractableResponse<Response> 최단거리_경로_조회(Long source, Long target) {
        return RestAssured
            .given().log().all()
            .queryParam("source", source)
            .queryParam("target", target)
            .when().get("/lines/paths")
            .then().log().all().extract();
    }
}
