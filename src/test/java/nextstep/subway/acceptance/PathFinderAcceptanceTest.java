package nextstep.subway.acceptance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathFinderAssertUtil.지하철_경로_조회_같은역으로_조회_예외;
import static nextstep.subway.acceptance.PathFinderAssertUtil.지하철_경로_조회_검증;
import static nextstep.subway.acceptance.PathFinderSteps.지하철_경로_조회;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;

@DisplayName("지하철 경로 조회")
public class PathFinderAcceptanceTest extends AcceptanceTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 죽전역;
    private Long 보정역;
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
        죽전역 = 지하철역_생성_요청("죽전역").jsonPath().getLong("id");
        보정역 = 지하철역_생성_요청("보정역").jsonPath().getLong("id");

        이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10);
        신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10);
        삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2);

        지하철_노선에_지하철_구간_생성_요청(삼호선, 남부터미널역, 양재역, 3);
    }

    /**
     * Given 지하철 노선과 구간 생성을 요청하고
     * When 출발역과 도착역의 경로를 조회하면
     * Then 출발역부터 도착역까지 최단거리 경로에 있는 역 목록을 조회할 수 있다.
     */
    @Test
    @DisplayName("지하철 경로 조회")
    void findPath() {
        // given
        // when
        var response = 지하철_경로_조회(교대역, 양재역);

        // then
        지하철_경로_조회_검증(response, 5, 교대역, 남부터미널역, 양재역);
    }

    /**
     * Given 지하철 노선과 구간 생성을 요청하고
     * When 출발역과 도착역이 같은 경로를 조회하면
     * Then 예외가 발생한다.
     */
    @Test
    @DisplayName("출발역과 도착역이 같은 경로 조회")
    void findPath_sameStation() {
        // given
        // when
        var response = 지하철_경로_조회(강남역, 강남역);

        // then
        지하철_경로_조회_같은역으로_조회_예외(response);
    }

    /**
     * Given 지하철 노선과 구간 생성을 요청하고
     * When 출발역과 도착역이 연결되어있지 않은 경로를 조회하면
     * Then 예외가 발생한다.
     */

    /**
     * Given 지하철 노선과 구간 생성을 요청하고
     * When 존재하지 않는 출발역이나 도착역을 조회하면
     * Then 예외가 발생한다.
     */
}
