package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_목록_조회_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_조회_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관리 기능")
class LineAcceptanceTest extends AcceptanceTest {
    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선 생성")
    @Test
    void createLine() {
        // given
        StationResponse 강남역 = StationSteps.지하철역_생성("강남역");
        StationResponse 양재역 = StationSteps.지하철역_생성("양재역");

        // when
        ExtractableResponse<Response> response = this.지하철_노선_생성_요청("2호선", "green", 강남역.getId(), 양재역.getId(), 3);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        ExtractableResponse<Response> listResponse = 지하철_노선_목록_조회_요청();

        assertThat(listResponse.jsonPath().getList("name")).contains("2호선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록 조회")
    @Test
    void getLines() {
        // given
        StationResponse 역삼역 = StationSteps.지하철역_생성("역삼역");
        StationResponse 선릉역 = StationSteps.지하철역_생성("선릉역");
        StationResponse 강남역 = StationSteps.지하철역_생성("강남역");
        StationResponse 양재역 = StationSteps.지하철역_생성("양재역");

        LineSteps.지하철_노선_생성("2호선", "green", 역삼역.getId(), 선릉역.getId(), 10);
        LineSteps.지하철_노선_생성("신분당선", "orange", 강남역.getId(), 양재역.getId(), 10);

        // when
        ExtractableResponse<Response> response = LineSteps.지하철_노선_목록_조회_요청();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("name")).contains("2호선", "신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선 조회")
    @Test
    void getLine() {
        // given
        StationResponse 강남역 = StationSteps.지하철역_생성("강남역");
        StationResponse 양재역 = StationSteps.지하철역_생성("양재역");

        LineResponse line = 지하철_노선_생성("2호선", "green", 강남역.getId(), 양재역.getId(), 10);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(line.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString("name")).isEqualTo("2호선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선 수정")
    @Test
    void updateLine() {
        // given
        StationResponse 강남역 = StationSteps.지하철역_생성("강남역");
        StationResponse 양재역 = StationSteps.지하철역_생성("양재역");

        LineResponse line = 지하철_노선_생성("2호선", "초록", 강남역.getId(), 양재역.getId(), 10);

        // when
        Map<String, String> params = new HashMap<>();
        params.put("color", "red");
        RestAssured
            .given()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put("/lines/{id}", line.getId())
            .then()
            .extract();

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(line.getId());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString("color")).isEqualTo("초록");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteLine() {
        // given
        StationResponse 강남역 = StationSteps.지하철역_생성("강남역");
        StationResponse 양재역 = StationSteps.지하철역_생성("양재역");

        LineResponse line = 지하철_노선_생성("2호선", "green", 강남역.getId(), 양재역.getId(), 10);

        // when
        ExtractableResponse<Response> response = RestAssured
            .given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete("/lines/{id}", line.getId())
            .then()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(
        final String name,
        final String color,
        final Long upStationId,
        final Long downStationId,
        final int distance
    ) {
        final var params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all().extract();
    }
}
