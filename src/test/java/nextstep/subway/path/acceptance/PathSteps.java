package nextstep.subway.path.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.dto.StationResponse;

public class PathSteps {

    public static ExtractableResponse<Response> 최단_경로_조회요청(StationResponse station1, StationResponse station2) {
        return RestAssured.given().log().all()
                .param("source", station1.getId())
                .param("target", station2.getId())
                .when()
                .get("/paths")
                .then().log().all()
                .extract();
    }
}
