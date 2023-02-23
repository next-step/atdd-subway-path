package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.PathSteps.*;
import static nextstep.subway.acceptance.StationSteps.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class PathAcceptanceTest extends AcceptanceTest {
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

        이호선 = 지하철_노선_생성_요청(createLineCreateParams("2호선", "green", 교대역, 강남역, 10)).jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청(createLineCreateParams("신분당선", "red", 강남역, 양재역, 10)).jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청(createLineCreateParams("3호선", "orange", 교대역, 남부터미널역, 2)).jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    /**
     * When 서로 다른 두 역에 대하여 최단거리 경로를 요청하면
     * Then 두 역을 포함한 최단 경로와 경로의 길이를 반환한다.
     */
    @DisplayName("두 역 간의 최단 경로를 조회한다.")
    @Test
    void getShortestPath() {
        ExtractableResponse<Response> response = 최단거리_경로_조회(교대역, 양재역);

        assertAll(
            () -> assertThat(response.jsonPath().getList("stations.id")).containsExactly(교대역, 남부터미널역, 양재역),
            () -> assertThat(response.jsonPath().getInt("distance")).isEqualTo(5)
        );
    }

    /**
     * When 출발역과 도착역이 같다면
     * Then 최단거리 경로를 구하는데 실패한다.
     */
    @DisplayName("출발역과 도착역이 같은 경우 경로 구하기 실패")
    @Test
    void getShortestPathWithSameStation() {
        ExtractableResponse<Response> response = 최단거리_경로_조회(교대역, 교대역);

        assertThat(response.statusCode()).isNotEqualTo(HttpStatus.OK.value());
    }

    /**
     * When 서로 다른 두 역에 대하여 연결되어 있지 않다면
     * Then 최단거리 경로를 구하는데 실패한다.
     */
    @DisplayName("경로가 연결되어 있지 않은 경우 경로 구하기 실패")
    @Test
    void getShortestPathWithSeperatedStations() {
        Long 무인도역 = 지하철역_생성_요청("무인도역").jsonPath().getLong("id");

        ExtractableResponse<Response> response = 최단거리_경로_조회(교대역, 무인도역);

        assertThat(response.statusCode()).isNotEqualTo(HttpStatus.OK.value());
    }

    /**
     * When 존재하지 않는 출발역이나 도착역인 경우
     * Then 최단거리 경로를 구하는데 실패한다.
     */
    @DisplayName("존재하지 않는 출발역이나 도착역을 조회할 경우 경로 구하기 실패")
    @Test
    void getShortestPathWithNonExistStation() {
        Long 존재하지_않는_역 = 10000L;
        ExtractableResponse<Response> response1 = 최단거리_경로_조회(교대역, 존재하지_않는_역);
        ExtractableResponse<Response> response2 = 최단거리_경로_조회(존재하지_않는_역, 교대역);

        assertAll(
            () -> assertThat(response1.statusCode()).isNotEqualTo(HttpStatus.OK.value()),
            () -> assertThat(response2.statusCode()).isNotEqualTo(HttpStatus.OK.value())
        );
    }

    private Map<String, String> createLineCreateParams(String name, String color, Long upStationId, Long downStationId, Integer distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, Integer distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
