package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.steps.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.steps.PathSteps.지하철_경로_조회_요청;
import static nextstep.subway.acceptance.steps.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 관련 기능")
public class PathAcceptanceTest extends AcceptanceTest {

    Long 강남역;
    Long 역삼역;
    Long 신논현역;

    @BeforeEach
    public void setUp() {
        super.setUp();
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        역삼역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        신논현역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 마지막 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("출발역으로부터 도착역까지의 경로에 있는 역 목록")
    @Test
    void path() {

        지하철_노선_생성_요청(createLineCreateParams(강남역, 역삼역)).jsonPath().getLong("id");
        지하철_노선_생성_요청(createLineCreateParams(강남역, 신논현역)).jsonPath().getLong("id");

        ExtractableResponse<Response> response = 지하철_경로_조회_요청(역삼역, 신논현역);

        assertThat(response.jsonPath().getInt("distance")).isEqualTo(20);
        assertThat(response.jsonPath().getList("paths.id", Long.class)).containsExactly(역삼역, 강남역, 신논현역);
    }

    private Map<String, String> createLineCreateParams(Long upStationId, Long downStationId) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신분당선");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", 10 + "");
        return lineCreateParams;
    }

}