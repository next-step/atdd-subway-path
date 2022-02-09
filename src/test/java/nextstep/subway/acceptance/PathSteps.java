package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class PathSteps {

    public static ExtractableResponse<Response> 지하철역_최단거리_경로_조회_요청(Long source, Long target) {
        return RestAssured
                .given().log().all()
                .when().get("/paths?source={source}&target={target}", source, target)
                .then().log().all().extract();
    }

    public static void 지하철역_최단거리_경로_조회_응답(
            ExtractableResponse<Response> response,
            int distance,
            Long... stations) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(stations);
        assertThat(response.jsonPath().getInt("distance")).isEqualTo(distance);
    }
}
