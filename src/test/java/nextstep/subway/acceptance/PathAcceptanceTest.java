package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.PathSteps.최단_경로_조회_요청;
import static nextstep.subway.acceptance.PathSteps.최단_경로_조회됨;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성;

@DisplayName("경로 조회 기능")
public class PathAcceptanceTest extends AcceptanceTest {
    private Long 강남역id;
    private Long 양재역id;
    private Long 교대역id;
    private Long 남부터미널역id;

    private Long 신분당선id;
    private Long 이호선id;
    private Long 삼호선id;

    /**
     * 교대역    --- *2호선*(5) ---   강남역
     * |                              |
     * *3호선*(2)                   *신분당선*(10)
     * |                              |
     * 남부터미널역  --- *3호선*(3) ---   양재
     *
     * 교대 - 강남 - 양재 15
     * 교대 - 남부터미널 - 양재 5
     */

    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역id = 지하철역_생성("강남역");
        양재역id = 지하철역_생성("양재역");
        교대역id = 지하철역_생성("교대역");
        남부터미널역id = 지하철역_생성("남부터미널역");

        신분당선id = 지하철_노선_생성("신분당선", "red", 강남역id, 양재역id, 10);
        이호선id = 지하철_노선_생성("2호선", "green", 교대역id, 강남역id, 5);
        삼호선id = 지하철_노선_생성("3호선", "orange", 교대역id, 남부터미널역id, 2);

        지하철_노선에_지하철_구간_생성_요청(삼호선id, createSectionCreateParams(남부터미널역id, 양재역id, 3));
    }

    /**
     * Scenario
     * Given 지하철 노선과 역들이 주어져 있을 때,
     * When 출발역과 도착역으로 경로 조회 요청을 하면
     * Then 경로가 조회된다.
     */
    @DisplayName("경로 조회")
    @Test
    void findPath() {
        // when
        ExtractableResponse<Response> response = 최단_경로_조회_요청(교대역id, 양재역id);

        // then
        최단_경로_조회됨(response, 교대역id, 남부터미널역id, 양재역id, 5);
    }
}
