package nextstep.subway.acceptance.path;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class PathUtils {

    public static ExtractableResponse<Response> 지하철역_경로조회(Long sourceStationId,
        Long targetStationId) {
        return RestAssured.given().log().all()
            .when()
            .queryParam("source", sourceStationId)
            .queryParam("target", targetStationId)
            .get("/paths")
            .then().log().all()
            .extract();
    }
}
