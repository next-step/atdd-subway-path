package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

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
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

        이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10).jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10).jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2).jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    /**
     * When 교대역 - 양재역 경로를 조회하면
     * Then 교대 - 남부터미널 - 양재 순으로 조회된다.
     */
    @DisplayName("가장 짧은 경로를 조회한다.")
    @Test
    void findShortestPath() {
        // when
        var response = 지하철_경로_조회(교대역, 양재역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).contains(교대역, 남부터미널역, 양재역);
        assertThat(response.jsonPath().getInt("distance")).isEqualTo(5);
    }

    /**
     * When 강남역 - 남부터미널역 경로를 조회하면
     * Then 강남 - 교대 - 남부터미널 순으로 조회된다.
     */
    @DisplayName("가장 짧은 경로를 조회한다 - 2")
    @Test
    void findShortestPath2() {
        // when
        var response = 지하철_경로_조회(강남역, 남부터미널역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).contains(강남역, 교대역, 남부터미널역);
        assertThat(response.jsonPath().getInt("distance")).isEqualTo(12);
    }

    private ExtractableResponse<Response> 지하철_경로_조회(Long source, Long target) {
        return RestAssured
                .given().log().all()
                .when()
                .queryParams("source", source)
                .queryParams("target", target)
                .get("/paths")
                .then().log().all().extract();
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
