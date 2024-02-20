package nextstep.subway.acceptance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nextstep.subway.dto.SectionRequest;

public class PathAcceptanceTest extends BaseAcceptanceTest {
    private Long 교대역_ID;
    private Long 강남역_ID;
    private Long 양재역_ID;
    private Long 남부터미널역_ID;
    private Long 이호선_ID;
    private Long 신분당선_ID;
    private Long 삼호선_ID;

    @BeforeEach
    void setUp() {
        교대역_ID = 지하철_역_생성(교대역);
        양재역_ID = 지하철_역_생성(양재역);
            남부터미널역_ID = 지하철_역_생성(남부터미널역);
        이호선_ID = 지하철_노선_생성_ID(getRequestParam("이호선", "초록색", 교대역_ID, 강남역_ID, 10));
        신분당선_ID = 지하철_노선_생성_ID(getRequestParam("신분당선", "빨간색", 강남역_ID, 양재역_ID, 10));
        삼호선_ID = 지하철_노선_생성_ID(getRequestParam("삼호선", "주황색", 교대역_ID, 남부터미널역_ID, 2));

        지하철_노선에_지하철_구간_생성_요청(삼호선_ID, new SectionRequest(남부터미널역_ID, 양재역_ID, 3));
    }

    @Test
    void test() {

    }
}
