package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

        Map<String, String> 이호선_초기설정 = createLineCreateParams("2호선", "green", 교대역, 강남역, 3);
        Map<String, String> 신분당선_초기설정 = createLineCreateParams("신분당선", "red", 강남역, 양재역, 8);
        Map<String, String> 삼호선_초기설정 = createLineCreateParams("3호선", "orange", 교대역, 남부터미널역, 4);

        이호선 = 지하철_노선_생성_요청(이호선_초기설정).jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청(신분당선_초기설정).jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청(삼호선_초기설정).jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 5));
    }

    /**
     * When 출발역-도착역 사이 경로조회 요청을 하면
     * Then 최단거리의 경로가 도출된다.
     */
    @DisplayName("최단거리 경로 조회")
    @Test
    void findShortestPath() {
        // when
        ExtractableResponse<Response> response = 지하철_최단거리_경로_조회_요청(교대역, 양재역);

        // then
        assertThat(response.jsonPath().getInt("distance")).isEqualTo(6);
        assertThat(response.jsonPath().getList("stations.id")).containsExactly(교대역, 남부터미널역, 양재역);
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