package nextstep.subway.unit;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 검색")
class PathAcceptanceTest extends AcceptanceTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath()
                .getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath()
                .getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath()
                .getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath()
                .getLong("id");

        이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10);
        신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10);
        삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2);

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParamsWithDistance(남부터미널역, 양재역, 3));
    }

    /**
     * Given 지하철 노선이 주어질때,
     * When 연결되어 있고 서로 다른 출발역과 도착역의 최단 거리를 구하면
     * Then 최단거리 및 지나온 경로의 역 목록을 확인할 수 있다.
     */
    @DisplayName("지하철 노선 경로 조회 기능")
    @Test
    void findPaths() {
        // Given
        // When
        var response = 지하철노선_최단경로_조회요청(교대역, 양재역);

        // Then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath()
                .getInt("distance")).isEqualTo(5);
        assertThat(response.jsonPath()
                .getList("stations.id", Long.class)).containsExactly(교대역, 남부터미널역, 양재역);
    }

    /**
     * Given 지하철 노선이 주어질때,
     * When 출발역과 도착역이 같은 경우,
     * Then 에러가 발생한다
     */
    @DisplayName("지하철 노선 경로 조회시, 출발역과 도착역이 같다.")
    @Test
    void findPaths_fail_source_target_equals() {
        // Given
        // When
        var response = 지하철노선_최단경로_조회요청(교대역, 교대역);

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선이 주어질때,
     * When 출발역과 도착역이 연결되어있지 않는 경우,
     * Then 에러가 발생한다
     */
    @DisplayName("지하철 노선 경로 조회시, 출발역과 도착역이 연결되어있지 않다.")
    @Test
    void findPaths_fail_source_target_not_connected() {
        // Given
        long 상도역 = 지하철역_생성_요청("상도역").jsonPath()
                .getLong("id");
        long 숭실대입구역 = 지하철역_생성_요청("숭실대입구역").jsonPath()
                .getLong("id");
        Long 칠호선 = 지하철_노선_생성_요청("7호선", "darkGreen", 상도역, 숭실대입구역, 10);

        // When
        var response = 지하철노선_최단경로_조회요청(교대역, 상도역);

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선이 주어질때,
     * When 존재하지 않은 출발역이나 도착역을 조회할 경우,
     * Then 에러가 발생한다
     */
    @DisplayName("지하철 노선 경로 조회시, 존재하지 않은 출발역을 조회한다.")
    @Test
    void findPaths_fail_station_is_not_included_() {
        // Given
        // When
        var response = 지하철노선_최단경로_조회요청(교대역, 0L);

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 지하철노선_최단경로_조회요청(Long source, Long target) {
        return RestAssured.given()
                .log()
                .all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .params("source", source, "target", target)
                .when()
                .get("/paths")
                .then()
                .log()
                .all()
                .extract();
    }

    private Map<String, String> createSectionCreateParamsWithDistance(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}