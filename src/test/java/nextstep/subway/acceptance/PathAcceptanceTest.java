package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathSteps.지하철역_최단거리_경로_조회_요청;
import static nextstep.subway.acceptance.PathSteps.지하철역_최단거리_경로_조회_응답;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 길찾기")
public class PathAcceptanceTest extends AcceptanceTest {

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

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

        Map<String, String> 이호선_정보 =
                createLineCreateParams("2호선", "green", 교대역, 강남역, 10);
        이호선 = 지하철_노선_생성_요청(이호선_정보).jsonPath().getLong("id");

        Map<String, String> 신분당선_정보 =
                createLineCreateParams("신분당선", "red", 강남역, 양재역, 10);
        신분당선 = 지하철_노선_생성_요청(신분당선_정보).jsonPath().getLong("id");

        Map<String, String> 삼호선_정보 =
                createLineCreateParams("3호선", "orange", 교대역, 남부터미널역, 2);
        삼호선 = 지하철_노선_생성_요청(삼호선_정보).jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    @DisplayName("지하철역 최단경로 찾기")
    @Test
    void 최단경로_찾기() {
        // when
        ExtractableResponse<Response> response = 지하철역_최단거리_경로_조회_요청(교대역, 양재역);

        // then
        지하철역_최단거리_경로_조회_응답(response, 5, 교대역, 남부터미널역, 양재역);
    }

    @DisplayName("지하철역 최단경로 찾을때 출발역과 도착역이 같은 경우")
    @Test
    void 출발역과_도착역이_같은_최단경로_찾기() {
        // when
        ExtractableResponse<Response> response = 지하철역_최단거리_경로_조회_요청(교대역, 교대역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역 최단경로 찾을때 출발역 또는 도착역이 존재하지 않는 경우")
    @Test
    void 출발역이_존재하지_않을때_최단경로_찾기() {
        Long 출발역 = 10L;

        // when
        ExtractableResponse<Response> response = 지하철역_최단거리_경로_조회_요청(출발역, 양재역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역 최단경로 찾을때 출발역 또는 도착역이 존재하지 않는 경우")
    @Test
    void 도착역이_존재하지_않을때_최단경로_찾기() {
        Long 도착역 = 10L;

        // when
        ExtractableResponse<Response> response = 지하철역_최단거리_경로_조회_요청(양재역, 도착역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역 최단경로 찾을때 출발역과 도착역이 연결되어 있지 않은 경우")
    @Test
    void 출발역과_도착역이_연결되지_않은_최단경로_찾기() {
        Long 곰역 = 지하철역_생성_요청("곰역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철역_최단거리_경로_조회_요청(곰역, 양재역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private Map<String, String> createLineCreateParams(String lineName, String lineColor, Long upStationId, Long downStationId, int distance) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", lineName);
        lineCreateParams.put("color", lineColor);
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", distance + "");
        return lineCreateParams;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
