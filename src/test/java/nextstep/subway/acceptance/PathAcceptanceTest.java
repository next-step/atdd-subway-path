package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathSteps.지하철_최단경로_조회;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static nextstep.subway.utils.AssertionUtils.목록은_다음을_순서대로_포함한다;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("최단 경로 조회 관련 기능")
public class PathAcceptanceTest extends AcceptanceTest {

    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * 교대역 --- *2호선(10)* --- 강남역
     * |                        |
     * *3호선(2)*             *신분당선(10)*
     * |                        |
     * 남부터미널역 --- *3호선(3)* -양재
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

        이호선 = 지하철_노선_생성_요청(new LineRequest("2호선", "green", 교대역, 강남역, 10)).jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청(new LineRequest("신분당선", "red", 강남역, 양재역, 10)).jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청(new LineRequest("3호선", "orange", 교대역, 남부터미널역, 2)).jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    /**
     * given: 출발역과 도착역으로(교대역 -> 양재역)
     * when : 최단 경로 조회를 요청하면
     * then : 최단 경로의 역 목록과 해당 경로의 총 거리를 얻을 수 있다.
     */
    @DisplayName("최단 경로 조회")
    @Test
    void path() {
        final ExtractableResponse<Response> response = 지하철_최단경로_조회(교대역, 양재역);

        final PathResponse path = response.as(PathResponse.class);
        final List<Long> 지하철_경로의_역_아이디_목록 = 지하철_경로에서_역_아이디를_순서대로_뽑는다(path);

        assertAll(
                () -> assertThat(path.getDistance()).isEqualTo(5),
                () -> 목록은_다음을_순서대로_포함한다(지하철_경로의_역_아이디_목록, 교대역, 남부터미널역, 양재역)
        );
    }

    private static List<Long> 지하철_경로에서_역_아이디를_순서대로_뽑는다(final PathResponse path) {
        return path.getStations()
                .stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
    }


    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
