package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
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
import static nextstep.subway.common.error.SubwayError.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 경로 찾기 관련 기능")
class PathAcceptanceTest extends AcceptanceTest {

    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 검암역;
    private Long 몽촌토성역;
    private Long 부평역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;
    private Long 일호선;

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
        검암역 = 지하철역_생성_요청("검암역").jsonPath().getLong("id");
        몽촌토성역 = 지하철역_생성_요청("몽촌토성역").jsonPath().getLong("id");
        부평역 = 지하철역_생성_요청("부평역").jsonPath().getLong("id");

        이호선 = 지하철_노선_생성_요청(createLineCreateParams("2호선", "green", 교대역, 강남역, 10)).jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청(createLineCreateParams("신분당선", "red", 강남역, 양재역, 10)).jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청(createLineCreateParams("3호선", "orange", 교대역, 남부터미널역, 2)).jsonPath().getLong("id");
        일호선 = 지하철_노선_생성_요청(createLineCreateParams("1호선", "orange", 검암역, 몽촌토성역, 5)).jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    /**
     * When 지하철 노선 기존 구간에 경로 찾기를 요청하면
     * Then 최적의 경로를 조회한다
     */
    @DisplayName("지하철 최적의 경로 찾기")
    @Test
    void showRoutes() {
        final ExtractableResponse<Response> 지하철_노선에_지하철_최적_경로_조회_응답 = 지하철_노선에_지하철_최적_경로_조회_요청(교대역, 양재역);

        assertAll(
                () -> assertThat(지하철_노선에_지하철_최적_경로_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(지하철_노선에_지하철_최적_경로_조회_응답.jsonPath().getList("stations.id", Long.class)).containsExactly(교대역, 남부터미널역, 양재역)
        );
    }

    /**
     * When 지하철 노선 기존 구간에 출발역과 도착역이 같은 경우 경로 찾기를 요청하면
     * Then 최적의 경로를 조회불가능하다
     */
    @DisplayName("출발역과 도착역이 같아서 조회가 불가능합니다")
    @Test
    void error_showRoutes() {
        final ExtractableResponse<Response> 지하철_노선에_지하철_최적_경로_조회_응답 = 지하철_노선에_지하철_최적_경로_조회_요청(교대역, 교대역);

        final JsonPath jsonPathResponse = 지하철_노선에_지하철_최적_경로_조회_응답.response().body().jsonPath();
        assertAll(
                () -> assertThat(지하철_노선에_지하철_최적_경로_조회_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(jsonPathResponse.getString("message")).isEqualTo(NO_FIND_SAME_SOURCE_TARGET_STATION.getMessage())
        );
    }

    /**
     * When 지하철 노선 기존 구간에 출발역과 도착역이 연결되어 있지 않은 경우 경로 찾기를 요청하면
     * Then 최적의 경로를 조회불가능하다
     */
    @DisplayName("출발역과 도착역이 연결되어 있지 않아서 조회가 불가능합니다")
    @Test
    void error_showRoutes_2() {
        final ExtractableResponse<Response> 지하철_노선에_지하철_최적_경로_조회_응답 = 지하철_노선에_지하철_최적_경로_조회_요청(교대역, 검암역);

        final JsonPath jsonPathResponse = 지하철_노선에_지하철_최적_경로_조회_응답.response().body().jsonPath();
        assertAll(
                () -> assertThat(지하철_노선에_지하철_최적_경로_조회_응답.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value()),
                () -> assertThat(jsonPathResponse.getString("message")).isEqualTo(NO_PATH_CONNECTED.getMessage())
        );
    }

    /**
     * When 지하철 노선 기존 구간에 출발역 또는 도착역이 등록되어 있지 않은 경우 경로 찾기를 요청하면
     * Then 최적의 경로를 조회불가능하다
     */
    @DisplayName("요청한 역이 노선의 등록되어 있지 않습니다")
    @Test
    void error_showRoutes_3() {
        final ExtractableResponse<Response> 지하철_노선에_지하철_최적_경로_조회_응답 = 지하철_노선에_지하철_최적_경로_조회_요청(교대역, 부평역);

        final JsonPath jsonPathResponse = 지하철_노선에_지하철_최적_경로_조회_응답.response().body().jsonPath();
        assertAll(
                () -> assertThat(지하철_노선에_지하철_최적_경로_조회_응답.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value()),
                () -> assertThat(jsonPathResponse.getString("message")).isEqualTo(NO_REGISTER_LINE_STATION.getMessage())
        );
    }

    private ExtractableResponse<Response> 지하철_노선에_지하철_최적_경로_조회_요청(final Long source, final Long target) {
        return RestAssured.given().log().all()
                .when().get("/paths?source={source}&target={target}", source, target)
                .then().log().all().extract();
    }

    private Map<String, Object> createSectionCreateParams(final Long upStationId, final Long downStationId, final Integer distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return params;
    }

    private Map<String, Object> createLineCreateParams(final String name, final String color, final Long upStationId, final Long downStationId, final Integer distance) {
        Map<String, Object> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", name);
        lineCreateParams.put("color", color);
        lineCreateParams.put("upStationId", upStationId);
        lineCreateParams.put("downStationId", downStationId);
        lineCreateParams.put("distance", distance);
        return lineCreateParams;
    }
}
