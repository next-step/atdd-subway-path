package nextstep.subway.acceptance.path;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class PathAcceptanceStep {

    public static ExtractableResponse<Response> 지하철_최단_경로_조회(Long source, Long target) {
        return RestAssured
                .given().log().all()
                .queryParam("source", source)
                .queryParam("target", target)
                .when().get("/paths")
                .then().log().all()
                .extract();
    }

}
