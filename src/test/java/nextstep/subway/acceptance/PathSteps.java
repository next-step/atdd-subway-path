package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class PathSteps extends Steps {

    static ExtractableResponse<Response> 역과_역의_최단거리_검색_요청(final long sourceStationId, final long targetStationId) {
        final Map<String, Object> params = new HashMap<>();
        params.put("source", sourceStationId);
        params.put("target", targetStationId);

        return RestAssured
                .given().log().all()
                .queryParams(params)
                .when().get("/paths")
                .then().log().all()
                .extract();
    }

    static void 최단거리가_정상적으로_검색되었는지_확인(final ExtractableResponse<Response> response, final int distance, final Long... stations) {
        응답_코드_검증(response, HttpStatus.OK);
        assertThat(response.jsonPath().getInt("distance")).isEqualTo(distance);
        assertThat(response.jsonPath().getList("id", Long.class)).containsExactly(stations);
    }
}
