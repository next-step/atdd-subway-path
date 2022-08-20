package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathSteps.지하철_최단거리_경로_조회_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("지하철 경로 조회 기능")
class PathAcceptanceTest extends AcceptanceTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 구일역;
    private Long 구로역;

    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;
    private Long 일호선;

    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");
        구일역 = 지하철역_생성_요청("구일역").jsonPath().getLong("id");
        구로역 = 지하철역_생성_요청("구로역").jsonPath().getLong("id");

        Map<String, String> 이호선_초기설정 = createLineCreateParams("2호선", "green", 교대역, 강남역, 3);
        Map<String, String> 신분당선_초기설정 = createLineCreateParams("신분당선", "red", 강남역, 양재역, 8);
        Map<String, String> 삼호선_초기설정 = createLineCreateParams("3호선", "orange", 교대역, 남부터미널역, 4);
        Map<String, String> 일호선_초기설정 = createLineCreateParams("2호선", "green", 구일역, 구로역, 6);


        이호선 = 지하철_노선_생성_요청(이호선_초기설정).jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청(신분당선_초기설정).jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청(삼호선_초기설정).jsonPath().getLong("id");
        일호선 = 지하철_노선_생성_요청(일호선_초기설정).jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 5));
    }

    /**
     * When 출발역-도착역 사이 경로조회 요청을 하면
     * Then 최단거리의 경로가 도출된다.
     */
    @DisplayName("출발역-도착역 최단거리 경로 조회")
    @Test
    void 최단거리_경로조회_성공() {
        // when
        ExtractableResponse<Response> response = 지하철_최단거리_경로_조회_요청(교대역, 양재역);

        // then
        assertThat(response.jsonPath().getDouble("distance")).isEqualTo(9);
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(교대역, 남부터미널역, 양재역);
    }

    /**
     * When 출발역과 도착역이 같은 상태로 경로조회 요청을 하면
     * Then 잘못된 요청으로 인해 에러가 발생한다.
     */
    @DisplayName("출발역-도착역 최단거리 경로 조회 실패(출발역과 도착역이 같음)")
    @Test
    void 최단거리_경로조회_출발역_도착역_같음() {
        // when
        ExtractableResponse<Response> response = 지하철_최단거리_경로_조회_요청(교대역, 교대역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * Given 새로운 지하철 역을 생성하고
     * When 노선에 등록되지 새로운 역을 출발점으로 하여 경로조회 시
     * Then 잘못된 요청으로 인해 에러가 발생한다.
     */
    @DisplayName("출발역-도착역 최단거리 경로 조회 실패(노선에 존재하지 않는 지하철역)")
    @Test
    void 최단거리_경로조회_노선에_존재하지_않는_지하철역() {
        // given
        Long 마곡나루역 = 지하철역_생성_요청("마곡나루역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_최단거리_경로_조회_요청(마곡나루역, 교대역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * When 연결되지 않은 두 역을 출발역-도착역으로 하여 경로조회 시
     * Then 잘못된 요청으로 인해 에러가 발생한다.
     */
    @DisplayName("출발역-도착역 최단거리 경로 조회 실패(연결되지 않은 출발역과 도착역)")
    @Test
    void 최단거리_경로조회_출발역_지하철역_연결안됨() {
        // when
        ExtractableResponse<Response> response = 지하철_최단거리_경로_조회_요청(구로역, 교대역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private Map<String, String> createLineCreateParams(String name, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", name);
        lineCreateParams.put("color", color);
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", distance + "");
        return lineCreateParams;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", 6 + "");
        return params;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }

}