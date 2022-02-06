package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Feature      : 경로 탐색 기능
 * background   : 아래의 그림과 같이 노선을 3개 생성한다.
 * <p>
 * (강남역) -6- (교대역) -4- (서초역)  <2호선 : 강남-교대-서초>
 * |           |
 * 10          7
 * \           /
 * (양재역)  -4- (매봉역)  <3호선 : 교대-양재-매봉>
 * /
 * 6
 * |
 * (양재시민의숲)
 * <신분당선 : 강남-양재-양재시민의숲>
 */
@DisplayName("경로 탐색 인수 테스트")
public class PathFinderAcceptanceTest extends AcceptanceTest {

    private Long 이호선;
    private Long 삼호선;
    private Long 신분당선;

    private Long 강남역;
    private Long 교대역;
    private Long 서초역;
    private Long 양재역;
    private Long 매봉역;
    private Long 양재시민의숲역;

    @BeforeEach
    void init() {
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        서초역 = 지하철역_생성_요청("서초역").jsonPath().getLong("id");

        Map<String, Object> 이호선_생성_body = createLineCreateParams("2호선", "green", 강남역, 교대역, 6);
        이호선 = 지하철_노선_생성_요청(이호선_생성_body).jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(이호선, createSectionCreateParams(교대역, 서초역, 4));

        Map<String, Object> 삼호선_생성_body = createLineCreateParams("3호선", "orange", 교대역, 매봉역, 11);
        삼호선 = 지하철_노선_생성_요청(삼호선_생성_body).jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(교대역, 양재역, 7));

        Map<String, Object> 신분당선_생성_body = createLineCreateParams("신분당선", "red", 강남역, 양재시민의숲역, 16);
        신분당선 = 지하철_노선_생성_요청(신분당선_생성_body).jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 양재시민의숲역, 6));
    }

    /**
     * Scenario : 출발역과 도착역을 입력해 경로를 탐색한다. - 정상
     * when     : 출발역과 도착역에 대해 경로 탐색 요청을 하면,
     * then     : 최단 경로를 응답해준다.
     */
    @Test
    @DisplayName("최단 경로를 탐색한다. - 정상 시나리오")
    void pathFind() {
        // when
        ExtractableResponse<Response> getResponse = PathFinderSteps.경로_탐색(createParams(강남역, 매봉역));

        // then
        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getResponse.body().jsonPath().getList("stations.name"))
                .containsExactly(Arrays.array("강남역", "양재역", "매봉역"));
    }

    /**
     * Scenario : 출발역과 도착역이 같으면 경로 탐색이 불가능하다.
     * when     : 같은 역을 출발역과 도착역으로 입력하면
     * then     : 최단 경로를 응답하지 않는다. (400에러)
     */
    @Test
    @DisplayName("최단 경로를 탐색하지 못한다. - 같은 역을 입력")
    void validatePathFind() {
        // when
        ExtractableResponse<Response> getResponse = PathFinderSteps.경로_탐색(createParams(강남역, 강남역));

        // then
        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Scenario : 출발역과 도착역이 이어져 있지 않다면 경로 탐색이 불가능하다.
     * given    : 새로운 역을 생성하고,
     * when     : 출발역과 새로운 역을 도착역으로 입력하면
     * then     : 최단 경로를 응답하지 않는다. (400에러)
     */
    @Test
    @DisplayName("최단 경로를 탐색하지 못한다. - 이어지지 않은 두 역을 입력")
    void validatePathFind2() {
        // given
        long 용산역 = 지하철역_생성_요청("용산역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> getResponse = PathFinderSteps.경로_탐색(createParams(강남역, 용산역));

        // then
        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Scenario : 존재하지 않은 역을 입력하면 경로 탐색이 불가능하다.
     * when     : 존재하지 않은 역을 출발역과 도착역에 입력하면
     * then     : 최단 경로를 응답하지 않는다. (404에러)
     */
    @Test
    @DisplayName("최단 경로를 탐색하지 못한다. - 존재하지 않은 역을 입력")
    void validatePathFind3() {
        Long 운정역 = 100L;

        // when
        ExtractableResponse<Response> getResponse = PathFinderSteps.경로_탐색(createParams(강남역, 운정역));

        // then
        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private Map<String, Object> createLineCreateParams(String name, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, Object> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", name);
        lineCreateParams.put("color", color);
        lineCreateParams.put("upStationId", upStationId);
        lineCreateParams.put("downStationId", downStationId);
        lineCreateParams.put("distance", distance);
        return lineCreateParams;
    }

    private Map<String, Object> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return params;
    }

    private Map<String, Object> createParams(Long 출발역, Long 도착역) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("source", 출발역);
        params.put("target", 도착역);

        return params;
    }
}
