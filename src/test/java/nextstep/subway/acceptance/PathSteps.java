package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

public class PathSteps {
    private static final String path = "/paths";

    public static ExtractableResponse<Response> 최단_경로_조회_요청(Long source, Long target) {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("source", source)
                .queryParam("target", target)
                .when().get(path)
                .then().log().all()
                .extract();
    }

    public static void 최단_경로_조회됨(ExtractableResponse<Response> response, Long 출발역, Long 중간역, Long 도착역, int distance) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class))
                .containsExactly(출발역, 중간역, 도착역);
        assertThat(response.jsonPath().getInt("distance")).isEqualTo(distance);
    }
}
