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

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 검색")
public class PathAcceptanceTest extends AcceptanceTest {
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
     * |                        |
     * | 2                      | 10
     * *3호선*                   *신분당선*
     * |                        |
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
     * Given 지하철 노선과 구간을 생성하고
     * When 지하철 경로를 조회하면
     * Then 출발역으로부터 도착역까지의 경로에 있는 역 목록과 조회한 경로 구간의 거리를 조회할 수 있다.
     */
    @DisplayName("지하철 경로를 조회한다")
    @Test
    void getPaths() {
        // when
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(교대역, 양재역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.name", String.class))
                .containsExactly("교대역", "남부터미널역", "양재역");
        assertThat(response.jsonPath().getInt("distance")).isEqualTo(5);
    }

    /**
     * Given 지하철 노선과 구간을 생성하고
     * When 지하철 경로 조회 시 출발역과 도착역이 같으면
     * Then 에러가 발생한다
     */
    @DisplayName("지하철 경로 조회 시 출발역과 도착역이 같으면 에러가 발생한다")
    @Test
    void getPaths_startAndEndStationEquals_Exception() {
        // when
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(교대역, 교대역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선과 구간을 생성하고
     * When 지하철 경로 조회 시 출발역과 도착역이 연결되어 있지 않으면
     * Then 에러가 발생한다
     */
    @DisplayName("지하철 경로 조회 시 출발역과 도착역이 연결되어 있지 않으면 에러가 발생한다")
    @Test
    void getPaths_startAndEndStationNotConnection_Exception() {
        // given
        Long 독바위역 = 지하철역_생성_요청("독바위역").jsonPath().getLong("id");
        Long 불광역 = 지하철역_생성_요청("불광역").jsonPath().getLong("id");
        지하철_노선_생성_요청("6호선", "brown", 독바위역, 불광역, 20);

        // when
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(교대역, 독바위역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선과 구간을 생성하고
     * When 지하철 경로 조회 시 출발역이 존재하지 않으면
     * Then 에러가 발생한다
     */
    @DisplayName("지하철 경로 조회 시 출발역 존재하지 않으면 에러가 발생한다")
    @Test
    void getPaths_startStationNotExist_Exception() {
        Long 존재하지_않는역 = 999L;

        // when
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(존재하지_않는역, 교대역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선과 구간을 생성하고
     * When 지하철 경로 조회 시 도착역이 존재하지 않으면
     * Then 에러가 발생한다
     */
    @DisplayName("지하철 경로 조회 시 도착역이 존재하지 않으면 에러가 발생한다")
    @Test
    void getPaths_EndStationNotExist_Exception() {
        Long 존재하지_않는역 = 999L;

        // when
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(교대역, 존재하지_않는역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 지하철_경로_조회_요청(Long source, Long target) {
        return RestAssured
                .given().log().all()
                .when()
                .get("/paths?source=" + source + "&target=" + target)
                .then().log().all()
                .extract();
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", String.valueOf(distance));
        return params;
    }
}
