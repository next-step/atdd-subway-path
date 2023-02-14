package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.exception.IdenticalSourceTargetNotAllowedException;
import nextstep.subway.exception.StationNotFoundException;

@DisplayName("지하철 경로 검색 기능")
class PathAcceptanceTest extends AcceptanceTest {

    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 매봉역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * Given 지하철 역과 노선 생성을 요청하고
     * 교대                         강남
     *  ● ────────── <2> ────────── ●
     *  └───────┐                   │
     *         <3>                  │
     *          └─────●─────┐    <신분당>
     *            남부터미널  │       │
     *                     <3>      │
     *                      └────── ● ────X────●
     *                             양재        매봉
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");
        매봉역 = 지하철역_생성_요청("매봉역").jsonPath().getLong("id");

        이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10);
        신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10);
        삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2);

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    /**
     * When 출발역과 도착역 사이의 경로 조회를 요청하면
     * Then 출발역과 도착역까지의 최단 경로가 조회된다.
     */
    @DisplayName("출발역과 도착역 사이의 최단 경로를 조회한다.")
    @Test
    void findPath() {
        // when
        ExtractableResponse<Response> response = 출발역과_도착역_사이의_경로_조회_요청(교대역, 양재역);

        // then
        assertAll(
            () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(교대역, 남부터미널역, 양재역),
            () -> assertThat(response.jsonPath().getInt("distance")).isEqualTo(5)
        );
    }

    /**
     * When 출발역과 도착역이 동일한 경로 조회 요청 시
     * Then 경로가 조회되지 않는다.
     */
    @DisplayName("지하철 경로 조회 시, 출발역과 도착역은 같지 않아야 한다.")
    @Test
    void identicalSourceTarget() {
        // when
        ExtractableResponse<Response> response = 출발역과_도착역_사이의_경로_조회_요청(교대역, 교대역);

        // then
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
            () -> assertThat(response.body().asString()).isEqualTo(
                String.format(IdenticalSourceTargetNotAllowedException.MESSAGE, "교대역", "교대역")
            )
        );
    }

    /**
     * When 출발역과 도착역이 연결되어 있지 않은 경로 조회 요청 시
     * Then 경로가 조회되지 않는다.
     */
    @DisplayName("지하철 경로 조회 시, 출발역과 도착역은 연결되어 있어야 한다.")
    @Test
    void notConnectedSourceTarget() {
        // when
        ExtractableResponse<Response> response = 출발역과_도착역_사이의_경로_조회_요청(교대역, 매봉역);

        // then
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
            () -> assertThat(response.body().asString()).isEqualTo("graph must contain the sink vertex")
        );
    }

    /**
     * When 출발역 또는 도착역이 존재하지 않은 경로 조회 요청 시
     * Then 경로가 조회되지 않는다.
     */
    @DisplayName("지하철 경로 조회 시, 출발역과 도착역은 모두 존재하는 역이어야 한다.")
    @Test
    void nonExistSourceTarget() {
        // when
        ExtractableResponse<Response> response = 출발역과_도착역_사이의_경로_조회_요청(교대역, 999L);

        // then
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
            () -> assertThat(response.body().asString()).isEqualTo(String.format(StationNotFoundException.MESSAGE, 999L))
        );
    }

    private ExtractableResponse<Response> 출발역과_도착역_사이의_경로_조회_요청(Long source, Long target) {
        return RestAssured
            .given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/paths?source={sourceId}&target={targetId}", source, target)
            .then().log().all().extract();
    }

    private Long 지하철_노선_생성_요청(String name, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", name);
        lineCreateParams.put("color", color);
        lineCreateParams.put("upStationId", String.valueOf(upStationId));
        lineCreateParams.put("downStationId", String.valueOf(downStationId));
        lineCreateParams.put("distance", String.valueOf(distance));
        return LineSteps.지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
