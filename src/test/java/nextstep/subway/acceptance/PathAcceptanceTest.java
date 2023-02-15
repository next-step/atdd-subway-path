package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("[Acceptance] 지하철 경로 검색 기능")
class PathAcceptanceTest extends AcceptanceTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * Given 노선을 아래와 같이 구성하고
     *
     *                       10
     *      교대역    ---   *2호선* ---   강남역
     *      |                                |
     *  1 *3호선*                       *신분당선*  10
     *      |                                |
     *      남부터미널역  --- *3호선* ---  양재역
     *                          2
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
        삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 1);
        지하철_노선에_지하철_구간_생성_요청(삼호선, 구간_요청(남부터미널역, 양재역, 2));
    }

    /**
     * When 경로를 조회하면
     * Then 출발역으로부터 도착역까지의 최단 경로를 확인할 수 있다
     */
    @DisplayName("지하철 경로 조회")
    @Test
    void getPaths() {
        // when
        var response = 지하철_경로를_조회한다(강남역, 남부터미널역);

        // then
        assertAll(() -> {
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 교대역, 남부터미널역);
            assertThat(response.jsonPath().getInt("distance")).isEqualTo(11);
        });
    }

    /**
     * Given 지하철 역, 구간, 노선을 더 추가하고
     *                       10                      1
     *      교대역    ---   *2호선* ---   강남역    ---    사당역
     *      |                                |                |
     *  1 *3호선*                       *신분당선* 10      *4호선* 1
     *      |                                |                |
     *      남부터미널역  --- *3호선* ---  양재역   ---    혜화역
     *                          2                    1
     * When 경로를 조회하면
     * Then 최단 경로를 확인할 수 있다
     */
    @DisplayName("복잡한 지하철 경로 조회")
    @Test
    void getComplexPath() {
        // Given
        Long 사당역 = 지하철역_생성_요청("사당역").jsonPath().getLong("id");
        Long 혜화역 = 지하철역_생성_요청("혜화역").jsonPath().getLong("id");
        지하철_노선_생성_요청("4호선", "blue", 사당역, 혜화역, 1);
        지하철_노선에_지하철_구간_생성_요청(이호선, 구간_요청(강남역, 사당역, 5));
        지하철_노선에_지하철_구간_생성_요청(삼호선, 구간_요청(양재역, 혜화역, 1));

        // when
        var response = 지하철_경로를_조회한다(교대역, 사당역);

        // then
        assertAll(() -> {
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(교대역, 남부터미널역, 양재역, 혜화역, 사당역);
            assertThat(response.jsonPath().getInt("distance")).isEqualTo(5);
        });
    }

    /**
     * Given 연결되어 있지 않은 독립적인 지하철 노선 추가하고
     *
     *                       10
     *      교대역    ---   *2호선* ---   강남역           사당역
     *      |                                |                |
     *  1 *3호선*                       *신분당선*  10     *4호선* 10
     *      |                                |                |
     *      남부터미널역  --- *3호선* ---  양재역          혜화역
     *                          2
     *
     * When 연결되어 있지 않은 경로를 조회하면
     * Then 경로를 조회할 수 없다
     */
    @DisplayName("출발역과 도착역이 연결되어 있지 않은 경우 지하철 경로 조회 실패")
    @Test
    void notConnectedStations() {
        // Given
        Long 사당역 = 지하철역_생성_요청("사당역").jsonPath().getLong("id");
        Long 혜화역 = 지하철역_생성_요청("혜화역").jsonPath().getLong("id");
        지하철_노선_생성_요청("4호선", "blue", 사당역, 혜화역, 10);

        // when
        var response = 지하철_경로를_조회한다(강남역, 혜화역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    // When 출발역과 도착역이 같은 경로를 조회하면
    // Then 경로를 조회할 수 없다.
    @DisplayName("출발역과 도착역이 동일한 경우 지하철 경로 조회 실패")
    @Test
    void sameStations() {
        // when
        var response = 지하철_경로를_조회한다(강남역, 강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    // When 존재하지 않는 역을 포함시켜 조회하면
    // Then 경로를 조회할 수 없다.
    @DisplayName("존재하지 않은 역을 포함시켜 조회하는 경우 지하철 경로 조회 실패")
    @Test
    void noStation() {
        // when
        Long 존재하지_않는_역_ID = Long.MAX_VALUE;
        var response = 지하철_경로를_조회한다(강남역, 존재하지_않는_역_ID);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private ExtractableResponse<Response> 지하철_경로를_조회한다(Long sourceStation, Long targetStation) {
        HashMap<String, Long> params = new HashMap<>();
        params.put("source", sourceStation);
        params.put("target", targetStation);

        return given().log().all()
                    .queryParams(params)
                .when()
                    .get("/paths")
                .then().log().all()
                .extract();
    }

    private Map<String, String> 구간_요청(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
