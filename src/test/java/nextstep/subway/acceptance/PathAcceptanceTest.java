package nextstep.subway.acceptance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.LineSteps.신규_라인;
import static nextstep.subway.acceptance.StationSteps.신규_지하철역;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;

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

        교대역 = 신규_지하철역("교대역");
        강남역 = 신규_지하철역("강남역");
        양재역 = 신규_지하철역("양재역");
        남부터미널역 = 신규_지하철역("남부터미널역");

        이호선 = 신규_지하철_노선(신규_라인("2호선", "green", 교대역, 강남역, 10L));
        신분당선 = 신규_지하철_노선(신규_라인("신분당선", "red", 강남역, 양재역, 10L));
        삼호선 = 신규_지하철_노선(신규_라인("3호선", "orange", 교대역, 남부터미널역, 2L));

        지하철_노선에_지하철_구간_생성_요청(삼호선, 신규_구간(남부터미널역, 양재역, 3L));
    }
}
