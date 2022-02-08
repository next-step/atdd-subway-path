package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.acceptance.AssertionSteps.응답_코드_검증;
import static org.assertj.core.api.Assertions.assertThat;

public class PathSteps {

    public static ExtractableResponse<Response> 최단_경로_조회(long 출발역, long 도착역) {
        return RestAssured.given().log().all()
                .queryParam("source", 출발역)
                .queryParam("target", 도착역)
                .when()
                .get("/paths")
                .then().log().all()
                .extract();
    }

    public static void 최단_경로_조회_실패(ExtractableResponse<Response> response) {
        응답_코드_검증(response, HttpStatus.CONFLICT);
    }

    public static void 최단_경로_역_목록_확인(ExtractableResponse<Response> response, Long... stations) {
        List<Long> idsOfStations = response.jsonPath().getList("stations.id", Long.class);
        assertThat(idsOfStations).containsExactly(stations);
    }

    public static void 최단_경로_거리_확인(ExtractableResponse<Response> response, int expected) {
        int distance = response.jsonPath().get("distance");
        assertThat(distance).isEqualTo(expected);
    }

}
