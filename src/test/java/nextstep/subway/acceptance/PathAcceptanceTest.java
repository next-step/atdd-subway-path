package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.line.LineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.handler.LineHandler.지하철_노선_생성_요청;
import static nextstep.subway.handler.LineHandler.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.handler.StationHandler.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

public class PathAcceptanceTest extends AcceptanceTest {

    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

        이호선 = 지하철_노선_생성_요청(createLineCreateParams("2호선", "green", 교대역, 강남역, 10))
                .jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청(createLineCreateParams("신분당선", "red", 강남역, 양재역, 10))
                .jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청(createLineCreateParams("3호선", "orange", 교대역, 남부터미널역, 2))
                .jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }


    /**
     * When 출발역과 도착역의 최단 경로를 조회하면
     * Then 두 역 사이의 경로에 있는 역 목록이 조회된다.
     */
    @DisplayName("출발역과 도착역 사이에 있는 역 목록을 조회한다.")
    @Test
    void findPath() {
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .queryParam("target", 양재역)
                .when().log().all()
                .get("/paths?source={source}", 교대역)
                .then().log().all()
                .extract();

        // then
        assertThat(response.jsonPath().getList("stations.id", Long.class))
                .containsExactly(교대역, 남부터미널역, 양재역);
    }

    private LineRequest createLineCreateParams(String lineName, String color,
                                               Long upStationId, Long downStationId, int distance) {
        return new LineRequest(lineName, color, upStationId, downStationId, distance);
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}