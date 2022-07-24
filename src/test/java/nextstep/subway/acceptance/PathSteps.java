package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

public class PathSteps {

    public static ExtractableResponse<Response> 지하철_최단_거리_조회_요청(Long sourceStationId, Long targetStationId) {
        return RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/paths?source={source}&target={target}", sourceStationId, targetStationId)
                .then().log().all()
                .extract();
    }
}
