package nextstep.subway.acceptance;

import nextstep.subway.acceptance.support.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static nextstep.subway.acceptance.support.LineSteps.createLineCreateParams;
import static nextstep.subway.acceptance.support.LineSteps.지하철_노선_생성_요청후_식별자_반환;
import static nextstep.subway.acceptance.support.SectionsSteps.createSectionCreateParams;
import static nextstep.subway.acceptance.support.SectionsSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.support.StationSteps.지하철역_생성_요청후_식별자_반환;

@DisplayName("지하철 경로 검색")
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

        교대역 = 지하철역_생성_요청후_식별자_반환("교대역");
        강남역 = 지하철역_생성_요청후_식별자_반환("강남역");
        양재역 = 지하철역_생성_요청후_식별자_반환("양재역");
        남부터미널역 = 지하철역_생성_요청후_식별자_반환("남부터미널역");

        이호선 = 지하철_노선_생성_요청후_식별자_반환(createLineCreateParams("2호선", "green", 교대역, 강남역, 10));
        신분당선 = 지하철_노선_생성_요청후_식별자_반환(createLineCreateParams("신분당선", "red", 강남역, 양재역, 10));
        삼호선 = 지하철_노선_생성_요청후_식별자_반환(createLineCreateParams("3호선", "orange", 교대역, 남부터미널역, 2));

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    @Nested
    class 최단경로찾기{
        @Test
        void 성공(){
        }

        @Test
        void 출발역과_도착역_같은경우(){
        }

        @Test
        void 출발역과_도착역이_연결_되어있지않는경우(){
        }
    }
}
