package nextstep.subway.path.acceptance.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

public class PathAcceptanceStep {
    public static ExtractableResponse<Response> 출발역에서_도착역까지의_최단_거리_경로_조회_요청(Long startStationId, Long endStationId) {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .param("startStationId", startStationId)
                .param("endStationId", endStationId)
                .get("/paths/shortest")
                .then()
                .log().all()
                .extract();
    }
}
