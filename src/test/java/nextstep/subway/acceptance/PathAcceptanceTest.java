package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.지하철_경로_조회;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 경로 검색")
class PathAcceptanceTest extends AcceptanceTest {

    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                             |
     * *3호선*                   *신분당선*
     * |                             |
     * 남부터미널역  --- *3호선* ---  양재
     */
    @Override
    @BeforeEach
    void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

        이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10).jsonPath().getLong("id");;
        신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10).jsonPath().getLong("id");;
        삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2).jsonPath().getLong("id");;

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    @DisplayName("경로 조회가 가능하다")
    @Test
    void 경로_조회() {
        final ExtractableResponse<Response> response = 지하철_경로_조회(강남역, 남부터미널역);

        경로에_최단거리_역들과_거리가_포함되어_있다(response, List.of(강남역, 교대역, 남부터미널역), 12);
    }

    private void 경로에_최단거리_역들과_거리가_포함되어_있다(
        ExtractableResponse<Response> response,
        List<Long> stationIds,
        int distance
    ) {
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsAll(stationIds),
            () -> assertThat(response.jsonPath().getInt("distance")).isEqualTo(distance)
        );
    }

    private Map<String, Object> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        return Map.of(
            "upStationId", upStationId,
            "downStationId", downStationId,
            "distance", distance
        );
    }
}
