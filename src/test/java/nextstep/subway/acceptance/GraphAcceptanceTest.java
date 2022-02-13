package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

public class GraphAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;
    private Long 이호선;
    private Long 삼호선;

    private Long 강남역;
    private Long 양재역;
    private Long 교대역;
    private Long 남부터미널역;

    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

        이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10).jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10).jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2).jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    /**
     * 교대역(1)    --- *2호선* ---   강남역(2)
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역(4)  --- *3호선* ---   양재(3)
     */

    /**
     * given 위와 같은 지하철 역이 주어졌을 때
     * when 교대역과 양재역의 최단거리를 구하면
     * Then
     * 1. 200 status code 가 반환된다.
     * 2. 지하철역들과 최단거리가 반환된다
     */
    @Test
    @DisplayName("역과 역 사이의 최단거리 구하기")
    void path() {
        ExtractableResponse<Response> response = 역과역_사이에_최단거리(1L,3L);
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getInt("distance")).isEqualTo(5);
        assertThat(response.jsonPath().getList("stations.id")).containsExactly(1,4,3);

    }

    /**
     * 교대역(1)    --- *2호선* ---   강남역(2)
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역(4)  --- *3호선* ---   양재(3)
     */

    /**
     * given 위와 같은 지하철 역이 주어졌을 때
     * when 교대역과 교대역의 최단거리를 구하면
     * Then 400 status code 가 반환된다.
     */
    @DisplayName("출발역과 도착역이 같은 경우")
    @Test
    void cantGetPathBySameStationException() {
        ExtractableResponse<Response> response = 역과역_사이에_최단거리(1L,1L);
        assertThat(response.statusCode()).isEqualTo(400);
    }

    /**
     * 교대역(1)    --- *2호선* ---   강남역(2)
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역(4)  --- *3호선* ---   양재(3)
     */

    /**
     * given 위와 같은 지하철 역이 주어졌을 때
     * when 교대역과 교대역의 최단거리를 구하면
     * Then 400 status code 가 반환된다.
     */
    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우")
    @Test
    void unConnectedSourceAndTargetException() {
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

        ExtractableResponse<Response> response = 역과역_사이에_최단거리(1L,5L);
        assertThat(response.statusCode()).isEqualTo(400);
    }

    /**
     * 교대역(1)    --- *2호선* ---   강남역(2)
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역(4)  --- *3호선* ---   양재(3)
     */

    /**
     * given 위와 같은 지하철 역이 주어졌을 때
     * when 교대역과 없는역의 최단거리를 구하면
     * Then 400 status code 가 반환된다.
     */
    @DisplayName("출발역 또는 도착역이 존재하지 않는 경우")
    @Test
    void NotExistedSourceOrTargetException() {
        ExtractableResponse<Response> response = 역과역_사이에_최단거리(1L,5L);
        assertThat(response.statusCode()).isEqualTo(400);
    }

    private Map<String, String> createSectionCreateParams(Long downStationId, Long upStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("downStationId", downStationId + "");
        params.put("upStationId", upStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
