package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathAcceptanceAssert.최단_경로_조회_검증;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("경로 조회 관련 기능")
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

        이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10).jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10).jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2).jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(삼호선, 남부터미널역, 양재역, 3);
    }

    /**
     * When 경로 조회를 요청하면
     * Then 출발역으로부터 도착역까지의 최단 경로에 있는 역 목록과
     * And 조회한 경로 구간의 거리를 반환한다.
     */
    @DisplayName("경로 조회를 요청하면 출발역으로부터 도착역까지의 최단 경로에 있는 역 목록과 조회한 경로 구간의 거리를 반환한다.")
    @Test
    void findShortestPath() {
        // given
        Long source = 강남역;
        Long target = 남부터미널역;
        List<Long> path = List.of(강남역, 교대역, 남부터미널역);
        long distance = 12L;

        // when, then
        최단_경로_조회_검증(source, target, path, distance);
    }

    /**
     * Given 경로 조회를 요청했을 때
     * Then 출발역과 도착역이 일치하면
     * And 에러 처리한다.
     */
    @DisplayName("경로 조회를 요청했을 때 출발역과 도착역이 일치하면 에러 처리한다.")
    @Test
    void findShortestPathIsSourceAndTargetEqual() {
        // given

        // when

        // then
    }

    /**
     * Given 경로 조회를 요청했을 때
     * Then 출발역과 도착역이 연결되어 있지 않으면
     * And 에러 처리한다.
     */
    @DisplayName("경로 조회를 요청했을 때 출발역과 도착역이 연결되어 있지 않으면 에러 처리한다.")
    @Test
    void findShortestPathIsSourceAndTargetNotConnection() {
        // given

        // when

        // then
    }

    @DisplayName("출발역 관련 에러")
    class SourceExceptionTest {
        /**
         * Given 경로 조회를 요청했을 때
         * Then 존재하지 않은 출발역으로 조회할 경우
         * And 에러 처리한다.
         */
        @DisplayName("경로 조회를 요청했을 때 존재하지 않은 출발역으로 조회할 경우 에러 처리한다.")
        @Test
        void findShortestPathIsSourceNotExist() {
            // given

            // when

            // then
        }

        /**
         * Given 경로 조회를 요청했을 때
         * Then 출발역을 상행역으로 하는 구간이 없는 경우
         * And 에러 처리한다.
         */
        @DisplayName("경로 조회를 요청했을 때 존재하지 않은 출발역으로 조회할 경우 에러 처리한다.")
        @Test
        void findShortestPathIsSourceNotExistSection() {
            // given

            // when

            // then
        }
    }

    @DisplayName("도착역 관련 에러")
    class TargetExceptionTest {
        /**
         * Given 경로 조회를 요청했을 때
         * Then 존재하지 않은 도착역으로 조회할 경우
         * And 에러 처리한다.
         */
        @DisplayName("경로 조회를 요청했을 때 존재하지 않은 도착역으로 조회할 경우 에러 처리한다.")
        @Test
        void findShortestPathIsTargetNotExist() {
            // given

            // when

            // then
        }

        /**
         * Given 경로 조회를 요청했을 때
         * Then 도착역을 하행역으로 하는 구간이 없는 경우
         * And 에러 처리한다.
         */
        @DisplayName("경로 조회를 요청했을 때 존재하지 않은 출발역으로 조회할 경우 에러 처리한다.")
        @Test
        void findShortestPathIsTargetNotExistSection() {
            // given

            // when

            // then
        }
    }
}
