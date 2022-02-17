package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 경로 검색")
public class PathAcceptanceTest extends AcceptanceTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 청량리역;
    private Long 회기역;

    private Long 일호선;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    private int 교대역_강남역_거리 = 10;
    private int 강남역_양재역_거리 = 5;
    private int 교대역_남부터미널역_거리 = 2;
    private int 남부터미널역_양재역_거리 = 3;
    private int 청량리역_회기역_거리 = 7;

    /**              (10)
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선* (2)               *신분당선* (5)
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
                     (3)          **/

    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");
        청량리역 = 지하철역_생성_요청("청량리역").jsonPath().getLong("id");
        회기역 = 지하철역_생성_요청("회기역").jsonPath().getLong("id");

        일호선 = 지하철_노선_생성_요청("1호선", "blue", 청량리역, 회기역, 청량리역_회기역_거리).jsonPath().getLong("id");
        이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 교대역_강남역_거리).jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 강남역_양재역_거리).jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 교대역_남부터미널역_거리).jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 남부터미널역_양재역_거리));
    }

    /**
     * Given 지하철 노선 (2호선, 3호선, 신분당선) 을 생성하고 역, 구간을 생성한다.
     * When 서로 다른 두 역의 최단 거리를 조회하면
     * Then 최단 거리 조회에 성공한다.
     */
    @DisplayName("최단 거리 조회하기")
    @Test
    void searchShortestPath() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("source", 남부터미널역.toString());
        params.put("target", 강남역.toString());

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .params(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/path")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(남부터미널역, 양재역, 강남역);
        assertThat(response.jsonPath().getInt("distance")).isEqualTo(남부터미널역_양재역_거리 + 강남역_양재역_거리);
    }

    /**
     * Given 지하철 노선 (2호선, 3호선, 신분당선) 을 생성하고 역, 구간을 생성한다.
     *       기존 노선과 연결되지 않는 새로운 노선 (1호선)을 생성한다.
     * When 연결 되지 않은 두 역의 최단 거리 조회를 요청 하면
     * Then 최단 거리 조회에 실패한다.
     */
    @DisplayName("최단 거리 조회하기 - 연결되지 않은 역을 조회 할 경우")
    @Test
    void searchShortestPathDoesNotExistPath() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("source", 강남역.toString());
        params.put("target", 청량리역.toString());

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .params(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/path")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());;
    }

    /**
     * Given 지하철 노선 (2호선, 3호선, 신분당선) 을 생성하고 역, 구간을 생성한다.
     * When 출발역과 도착역이 동일한데 최단 거리 조회를 요청하면
     * Then 최단 거리 조회에 실패한다.
     */
    @DisplayName("최단 거리 조회하기 - 출발역과 도착역이 동일한 경우")
    @Test
    void searchShortestPathSourceEqualsTarget() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("source", 강남역.toString());
        params.put("target", 강남역.toString());

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .params(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/path")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
