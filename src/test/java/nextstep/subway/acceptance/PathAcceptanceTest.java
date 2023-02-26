package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathSteps.지하철_노선_최단거리_조회;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static nextstep.subway.fixture.PathFixtures.PATH_ID;

public class PathAcceptanceTest extends AcceptanceTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * 교대역    --- *2호선(10km)*  ---    강남역
     * |                                  |
     * *3호선(2km)*                   *신분당선(10km)*
     * |                                  |
     * 남부터미널역  --- *3호선(3km)*   ---  양재
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");
        이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10).jsonPath().getLong(PATH_ID);
        신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10).jsonPath().getLong(PATH_ID);
        삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2).jsonPath().getLong(PATH_ID);
        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    /**
     * when : 출발역과 도착역을 넣어 지하철 노선의 최단거리를 조회하면
     * then : 출발역과 도착역 사이의 최단 거리의 경로와 거리를 확인할 수 있다.
     */
    @Test
    void 최단_경로를_조회_한다() {
        //when
        Long 출발역 = 강남역;
        Long 도착역 = 남부터미널역;
        ExtractableResponse<Response> response = 지하철_노선_최단거리_조회(출발역, 도착역);

        //then
        Assertions.assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 교대역, 남부터미널역);
        Assertions.assertThat(response.jsonPath().getInt("distance")).isEqualTo(12);
    }
}
