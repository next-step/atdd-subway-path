package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class PathSteps {
    public static ExtractableResponse<Response> 최단_경로_조회(Long startStationId, Long endStationId) {
        Map<String, String> params = new HashMap<>();
        params.put("source", startStationId + "");
        params.put("target", endStationId + "");


        return RestAssured
                .given().log().all()
                .params(params)
                .when().get("/paths")
                .then().log().all().extract();
    }

    public static void 최단_경로_검증(ExtractableResponse<Response> response, int pathSize, List<String> pathNames, int distance) {
        assertThat(response.jsonPath().getList("stations")).hasSize(pathSize);
        assertThat(response.jsonPath().getList("stations.name")).containsExactly(pathNames);
        assertThat(response.jsonPath().getInt("stations.distance")).isEqualTo(distance);
    }

    public static void 최단_경로_조회_예외_검증(ExtractableResponse<Response> response, String message) {
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST);
        assertThat(response.jsonPath().getString("message")).isEqualTo(message);
    }
}
