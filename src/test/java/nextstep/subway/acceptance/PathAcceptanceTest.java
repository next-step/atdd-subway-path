package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;

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
     * 교대역 --- *2호선(10)* ---   강남역
     * |                        |
     * *3호선(2)*                *신분당선(10)*
     * |                        |
     * 남부터미널역 --- *3호선(3)* --- 양재
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

        이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10)
                .jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10)
                .jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2)
                .jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(삼호선, 남부터미널역, 양재역, 3);
    }

    /**
     * When 출발역과 도착역에 대한 경로 검색을 요청하면
     * Then 최단 거리 내의 지하철역 목록과 거리 길이를 응답받는다
     */
    @Test
    @DisplayName("출발역과 도착역으로 경로의 최단 거리 검색")
    void getShortestPaths() {
        // when
        ExtractableResponse<Response> response = 최단_거리_경로_검색_요청(교대역, 양재역);

        // then
        Long[] pathIds = {교대역, 남부터미널역, 양재역};
        최단_거리_경로_검색_완료(response, pathIds);
    }

    /**
     * When 같은 출발역과 도착역에 대한 경로 검색을 요청하면
     * Then 경로 검색에 실패한다
     */
    @Test
    @DisplayName("같은 출발역과 도착역으로 경로의 최단 거리 검색")
    void getShortestPathsWithSameStation() {
        // when
        ExtractableResponse<Response> response = 최단_거리_경로_검색_요청(교대역, 교대역);

        // then
        PathSteps.최단_거리_경로_검색_실패(response);
    }

    /**
     * When 연결되어 있지 않은 출발역과 도착역에 대한 경로 검색을 요청하면
     * Then 경로 검색에 실패한다
     */
    @Test
    @DisplayName("연결되어 있지 않은 출발역과 도착역으로 경로의 최단 거리 검색")
    void getShortestPathsWhenNotConnected() {
        // given
        Long 서울역 = 지하철역_생성_요청("서울역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 최단_거리_경로_검색_요청(교대역, 서울역);

        // then
        최단_거리_경로_검색_실패(response);
    }

    /**
     * When 존재하지 않는 출발역으로 도착역에 대한 경로 검색을 요청하면
     * Then 경로 검색에 실패한다
     */
    @Test
    @DisplayName("존재하지 않는 출발역으로 도착역에 대한 경로의 최단 거리 검색")
    void getShortestPathsWithInvalidSource() {
        // when
        ExtractableResponse<Response> response = 최단_거리_경로_검색_요청(교대역, 0L);

        // then
        최단_거리_경로_검색_실패(response);
    }

    /**
     * When 출발역과 존재하지 않는 도착역으로 경로의 최단 거리 검색을 요청하면
     * Then 경로 검색에 실패한다
     */
    @Test
    @DisplayName("출발역과 존재하지 않는 도착역으로 경로의 최단 거리 검색")
    void getShortestPathsWithInvalidTarget() {
        // when
        ExtractableResponse<Response> response = 최단_거리_경로_검색_요청(0L, 교대역);

        // then
        최단_거리_경로_검색_실패(response);
    }
}
