package nextstep.subway.acceptance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;

@DisplayName("지하철 경로 검색")
class PathAcceptanceTest extends AcceptanceTest {

    private Long 신논현역;
    private Long 강남역;
    private Long 양재역;
    private Long 도곡역;
    private Long 선릉역;

    private Long 신분당선;
    private Long 삼호선;
    private Long 분당선;

    /**
     * 신논현역                     도곡역  --- *분당선* ---  선릉역
     *   |                           |
     * *신분당선*                  *삼호선*
     *   |                           |
     * 강남역  --- *신분당선* ---  양재역
     */

    @BeforeEach
    public void setUp() {
        super.setUp();

        신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        도곡역 = 지하철역_생성_요청("도곡역").jsonPath().getLong("id");
        선릉역 = 지하철역_생성_요청("선릉역").jsonPath().getLong("id");

        신분당선 = 지하철_노선_생성_요청("신분당선", "bg-red-600", 신논현역, 강남역, 10).jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청("삼호선", "bg-orange-600", 양재역, 도곡역, 20).jsonPath().getLong("id");
        분당선 = 지하철_노선_생성_요청("분당선", "bg-yellow-600", 도곡역, 선릉역, 25).jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 15));
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }

}
