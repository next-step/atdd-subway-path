package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class PathSteps {
    public static ExtractableResponse<Response> 지하철_경로_조회_요청(Long 출발역, Long 도착역) {
        Map<String, String> params = new HashMap<>();
        params.put("source", 출발역 + "");
        params.put("target", 도착역 + "");
        return RestAssured
                .given().log().all()
                .params(params)
                .when().get("/paths")
                .then().log().all().extract();
    }

    public static void 지하철_경로_조회됨(ExtractableResponse<Response> response, List<Long> stationIds, int distance) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactlyElementsOf(stationIds);
        assertThat(response.jsonPath().getInt("distance")).isEqualTo(distance);
    }
}
