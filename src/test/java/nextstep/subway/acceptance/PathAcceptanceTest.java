package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.ui.response.PathResponse;
import nextstep.subway.ui.response.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.acceptance.LineSectionAcceptanceTest.createSectionCreateParams;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathSteps.지하철_경로_조회_요청;
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
     * (10)
     * 교대역    ---   *2호선*   ---  강남역
     * |                                |
     * *3호선* (2)                    *신분당선* (10)
     * |                  (3)           |
     * 남부터미널역  --- *3호선* --- 양재역
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

        이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10);
        신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10);
        삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2);

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3L));
    }

    /**
     * Given 지하철 노선에 구간을 등록한다.
     * When 출발역과 도착역을 입력받아 최단 거리 경로를 조회한다.
     * Then 최단 거리 경로를 반환한다.
     */
    @DisplayName("최단 거리 경로 조회")
    @Test
    public void findPath() {
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(교대역, 양재역);

        var pathResponse = response.as(PathResponse.class);
        assertThat(pathResponse.getDistance()).isEqualTo(5);
        assertThat(pathResponse.getStations().stream().map(StationResponse::getId)).containsExactly(교대역, 남부터미널역, 양재역);
    }
}
