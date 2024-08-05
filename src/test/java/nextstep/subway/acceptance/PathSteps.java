package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

public class PathSteps {
    static ExtractableResponse<Response> 경로_찾기(Long sourceStationId, Long targetStationId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/paths?source=" + sourceStationId + "&target=" + targetStationId)
                .then().log().all()
                .extract();
    }

}
