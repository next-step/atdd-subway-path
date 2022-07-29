package nextstep.subway.path.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.steps.LineSectionSteps.*;
import static nextstep.subway.steps.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 조회 기능")
class PathAcceptanceTest extends AcceptanceTest {

    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /** GIVEN
     *
     * 교대역    ---    *2호선* ---   강남역
     *   |                           |
     * *3호선*                     *신분당선*
     *   |                           |
     * 남부터미널역  --- *3호선* ----   양재
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

        이호선 = 지하철_노선_생성_요청("2호선", "green").jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청("신분당선", "red").jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청("3호선", "orange").jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(이호선, createSectionCreateParams(교대역, 강남역, 6));
        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 7));
        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(교대역, 남부터미널역, 2));
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 2));
    }

    @DisplayName("출발역과 도착역이 이어져있을 때 경로를 조회하면 경로와 최단거리를 응답받는다.")
    @Test
    void 경로_조회() {
        // when
        var response = 경로를_조회한다(교대역, 양재역);

        // then
        경로정보가_일치한다(response, 8, 교대역, 강남역, 양재역);
    }

    private void 경로정보가_일치한다(ExtractableResponse<Response> response, int distance, Long... stationIds) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        assertThat(response.jsonPath().getInt("distance")).isEqualTo(distance);
        assertThat(response.jsonPath().getList("stations.id", Long.class))
                .containsExactly(stationIds);
    }

    private ExtractableResponse<Response> 경로를_조회한다(Long source, Long target) {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .params(createPathParams(source, target))
                .when().get("/paths")
                .then().log().all()
                .extract();
    }

    private Map<String, String> createPathParams(Long source, Long target) {
        Map<String, String> params = new HashMap<>();
        params.put("source", source + "");
        params.put("target", target + "");
        return params;
    }
}
