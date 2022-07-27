package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.PathSteps.지하철_최소_경로_조회_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 최소경로 기능")
public class PathAcceptanceTest extends AcceptanceTest {

    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;


    /**
     * 교대역        ---   *2호선*   ---   강남역
     * |                                   |
     * *3호선*                             *신분당선*
     * |                                   |
     * 남부터미널역  ---   *3호선*   ---   양재역
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청함("교대역");
        강남역 = 지하철역_생성_요청함("강남역");
        양재역 = 지하철역_생성_요청함("양재역");
        남부터미널역 = 지하철역_생성_요청함("남부터미널역");

        이호선 = 지하철_노선_생성_요청함("2호선", "green", 교대역, 강남역, 10);
        신분당선 = 지하철_노선_생성_요청함("신분당선", "red", 강남역, 양재역, 10);
        삼호선 = 지하철_노선_생성_요청함("3호선", "orange", 교대역, 남부터미널역, 2);

        지하철_노선에_지하철_구간_생성_요청함(삼호선, 남부터미널역, 양재역, 3);
    }

    /**
     * When 출발역과 도착역의 최소 경로를 요청하면
     * Then 최소 경로 구간을 조회할 수 있다.
     */
    @Test
    @DisplayName("최소경로 조회")
    void findShortPath() {
        // when
        ExtractableResponse<Response> 최소경로_정보 = 지하철_최소_경로를_조회함(교대역, 양재역);

        // then
        최소_경로를_찾을수있음(최소경로_정보, 5, 교대역, 남부터미널역, 양재역);
    }

    private Long 지하철역_생성_요청함(String name) {
        return 지하철역_생성_요청(name).jsonPath().getLong("id");
    }

    private Long 지하철_노선_생성_요청함(String name, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, String> lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", name);
        lineCreateParams.put("color", color);
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", distance + "");

        return 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    private void 지하철_노선에_지하철_구간_생성_요청함(Long lineId, Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");

        지하철_노선에_지하철_구간_생성_요청(lineId, params);
    }

    private ExtractableResponse<Response> 지하철_최소_경로를_조회함(Long source, Long target) {
        ExtractableResponse<Response> response = 지하철_최소_경로_조회_요청(source, target);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        return response;
    }

    private void 최소_경로를_찾을수있음(ExtractableResponse<Response> response, int distance, Long... stationIds) {
        assertAll(() -> {
            assertThat(response.jsonPath().getList("id", Long.class)).containsExactly(stationIds);
            assertThat(response.jsonPath().getInt("distance")).isEqualTo(distance);
        });
    }
}
