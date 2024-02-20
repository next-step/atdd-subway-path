package nextstep.subway.acceptance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    @DisplayName("   given 출발역과 도착역이 주어질 때\n"
                 + "   when 경로를 조회하면\n"
                 + "   then 출발역과 도착역 사이 역 목록과\n"
                 + "   and  출발역과 도착역 사이 거리를 조회할 수 있다.")
    @Test
    void 경로를_조회하면_출발역과_도착역_사이_역_목록과_거리를_조회할_수_있다() {

    }

    @DisplayName("given 출발역과 도착역이 주어질 때\n"
                 + "    when 경로를 조회하는데\n"
                 + "    and  출발역과 도착역이 동일하면\n"
                 + "    then 예외를 반환한다.")
    @Test
    void 경로를_조회할_때_출발역과_도착역이_동일하면_예외를_반환한다() {

    }

    @DisplayName("given 출발역과 도착역이 주어질 때\n"
                 + "    when 경로를 조회하는데\n"
                 + "    and  출발역과 도착역이 연결되어 있지 않으면\n"
                 + "    then 예외를 반환한다.")
    @Test
    void 경로를_조회할_때_출발역과_도착역이_연결되어있지_않으면_예외를_반환한다() {

    }

    @DisplayName("given 출발역과 도착역이 주어질 때\n"
                 + "    when 경로를 조회하는데\n"
                 + "    and  출발역 또는 도착역이 존재하지 않으면\n"
                 + "    then 예외를 반환한다.")
    @Test
    void 경로를_조회할_때_출발역_또는_도착역이_존재하지_않으면_예외를_반환한다() {

    }
}
