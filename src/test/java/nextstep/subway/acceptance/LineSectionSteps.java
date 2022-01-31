package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_조회_요청;
import static org.assertj.core.api.Assertions.assertThat;

public class LineSectionSteps {
    public static void 노선_조회_검증(Long lineId, Long startStationId, Long centerStationId, Long endStationId) {
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(startStationId, centerStationId, endStationId);
    }

    public static void 노선_조회_검증(Long lineId, Long startStationId, Long endStationId) {
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(startStationId, endStationId);
    }
}
