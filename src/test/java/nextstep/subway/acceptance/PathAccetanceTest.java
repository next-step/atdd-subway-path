package nextstep.subway.acceptance;


import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.steps.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.steps.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.steps.PathSteps.지하철_경로_최단_거리_조회_요청;
import static nextstep.subway.acceptance.steps.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 경로 관련 기능")
public class PathAccetanceTest extends AcceptanceTest {


    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * 10
     * 교대역    --- *2호선* ---   강남역
     * |                          |
     * *3호선* 2                  *신분당선* 10
     * |                           |
     * 남부터미널역  --- *3호선* ---   양재
     * 3
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

        이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10);
        신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10);
        삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2);

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }


    /**
     * When: 경로를 조회하면
     * Then: 경로의 최단거리가 조회된다.
     */
    @Test
    @DisplayName("최단 거리가 조회된다.")
    void getPaths() {
        // when
        ExtractableResponse<Response> response = 지하철_경로_최단_거리_조회_요청(교대역, 양재역);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(교대역, 남부터미널역, 양재역),
                () -> assertThat(response.jsonPath().getInt("distance")).isEqualTo(5)
        );
    }

    /**
     * Given: 신규 역과 노선을 생성하고
     * When: 연결되어 있지 않은 경로를 조회하면
     * Then: 경로 조회에 실패한다.
     */
    @Test
    @DisplayName("연결되어 있지 않은 경로는 경로 조회에 실패한다.")
    void getPathsFail() {
        // given
        Long 신규상행역 = 지하철역_생성_요청("신규상행역").jsonPath().getLong("id");
        Long 신규하행역 = 지하철역_생성_요청("신규하행역").jsonPath().getLong("id");
        지하철_노선_생성_요청("신규호선", "new-color", 신규상행역, 신규하행역, 10);

        // when
        ExtractableResponse<Response> response = 지하철_경로_최단_거리_조회_요청(교대역, 신규하행역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
