package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.Map;

public class PathSteps {

    public static ExtractableResponse<Response> 지하철_경로_조회(Long source, Long target) {
        return RestAssured
                .given().log().all()
                .queryParams(Map.of(
                        "source",source,
                        "target", target
                ))
                .when().get("/paths")
                .then().log().all().extract();
    }
}
