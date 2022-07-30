package nextstep.subway.acceptance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.SectionSteps.createSectionCreateParams;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;

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

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    /**
     * When 교대역에서 강남역의 최단 거리를 조회할 때,
     * Then 최단 거리는 10입니다.
     */
    @Test
    void test1() {

    }

    /**
     * When 교대역에서 양재역까지 최단 경로는 조회할 때
     * Then 최단 경로는교대역 - 남부터미널역 - 양재역이고
     * Then 거리는 12 입니다.
     */
    @Test
    void test2() {

    }

    /**
     * When 출발역과 도착역을 같게 조회하면
     * Then 예외가 발생합니다.
     */
    @Test
    void test3() {

    }

    /**
     * When 출발역과 도착역이 연결이 되어 있지 않은 경우를 조회하면
     * Then 예외가 발생합니다.
     */
    @Test
    void test4() {

    }

    /**
     * When 존재하지 않은 출발역이나 도착역을 조회 할 경우
     * Then 예외가 발생합니다.
     */
    @Test
    void test5() {

    }
}
