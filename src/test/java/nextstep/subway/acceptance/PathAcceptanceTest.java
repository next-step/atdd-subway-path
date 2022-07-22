package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 탐색 기능")
class PathAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;

    private Long 신논현역;
    private Long 강남역;
    private Long 양재역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = 노선생성파라미터(강남역, 양재역);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(신분당선, 구간생성파라미터(신논현역, 강남역));

    }

    /**
     * Given 한 역과 또 다른 역에 대해
     * When 최단 경로 조회를 요청하면
     * Then 경로 구간에 있는 역 목록과 거리가 조회된다.
     */
    @DisplayName("두 역 사이의 최단 경로를 조회한다.")
    @Test
    void findPath() {
        // when
        ExtractableResponse<Response> response = 최단경로_조회_요청(신논현역, 양재역);

        // then
        최단거리조회가_정상수행되었는지검증(response);
    }

    /**
     * Given 연결이 되어 있지 않은 두 역에 대해
     * When 최단 경로 조회를 요청하면
     * Then 조회에 실패한다.
     */
    @DisplayName("연결이 되어 있지 두 역 사이의 최단 경로를 조회한다.")
    @Test
    void findPath_DisconnectedStations() {
        // given
        Long 잠실역 = 지하철역_생성_요청("잠실역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 최단경로_조회_요청(잠실역, 양재역);

        // then
        최단거리조회가_실패했는지검증(response);
    }

    /**
     * Given 동일한 두 역에 대해
     * When 최단 경로 조회를 요청하면
     * Then 조회에 실패한다.
     */
    @DisplayName("동일한 두 역 사이의 최단 경로를 조회한다.")
    @Test
    void findPath_FromStationAndToStationEquals() {
        // when
        ExtractableResponse<Response> response = 최단경로_조회_요청(양재역, 양재역);

        // then
        최단거리조회가_실패했는지검증(response);
    }

    /**
     * Given 존재하지 않는 역에 대해
     * When 최단 경로 조회를 요청하면
     * Then 조회에 실패한다.
     */
    @DisplayName("존재하지 않는 역 사이의 최단 경로를 조회한다.")
    @Test
    void findPath_NotExistsStation() {
        // when
        ExtractableResponse<Response> response = 최단경로_조회_요청(100L, 양재역);

        // then
        최단거리조회가_실패했는지검증(response);
    }

    private ExtractableResponse<Response> 최단경로_조회_요청(final Long 출발역, final Long 도착역) {
        final Map<String, Long> params = new HashMap<>();
        params.put("source", 출발역);
        params.put("target", 도착역);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/paths")
                .then().log().all()
                .extract();
    }

    private void 최단거리조회가_정상수행되었는지검증(final ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getList("stations.id", Long.class)).isNotEmpty();
    }

    private void 최단거리조회가_실패했는지검증(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

}
