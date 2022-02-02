package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathAcceptanceTest extends AcceptanceTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 잠실역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * 교대역    --- *2호선*(10) ---   강남역
     * |                              |
     * *3호선(2)*                   *신분당선*(10)
     * |                              |
     * 남부터미널역 -- *3호선*(3)--   양재
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");
        잠실역 = 지하철역_생성_요청("잠실역").jsonPath().getLong("id");

        이호선 = 노선_생성_요청("2호선", "bg-red-600", 교대역, 강남역, 10);
        신분당선 = 노선_생성_요청("신분당선", "red", 강남역, 양재역, 10);
        삼호선 = 노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2);

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    /**
     * When 최단거리 조회 요청을 하면
     * Then 최단거리에 대한 응답을 받는다.
     */
    @DisplayName("최단거리 조회")
    @ParameterizedTest(name = "{0}에서 {1}까지의 최단거리는 {2}이고 경로는: {3}이다.")
    @MethodSource("generateData")
    void searchShortestPath(String sourceStationName, String targetStationName, String distance, List<String> pathStationNames) {
        //when
        ExtractableResponse<Response> response = 최단_경로_조회_요청(sourceStationName, targetStationName);

        //then
        List<Long> pathStationIds = pathStationNames.stream()
                .map(this::stationId)
                .collect(Collectors.toList());

        최단거리_조회_응답_확인(response, distance, pathStationIds);
    }

    /**
     * When 출발역과 도착역이 같을때 최단거리 조회 요청을 하면
     * Then 요청이 실패한다.
     */
    @DisplayName("출발역과 도착역이 같은 경우 최단거리 조회는 불가능하다.")
    @Test
    void searchShortestPath2() {
        //when
        ExtractableResponse<Response> response = 최단_경로_조회_요청("강남역", "강남역");

        //then
        최단거리_조회_요청_실패(response);
    }

    /**
     * When 출발역과 도착역이 연결되어 있지 않은 경우 최단거리 조회 요청을 하면
     * Then 요청이 실패한다.
     */
    @DisplayName("출발역과 도착역이 연결되어 있지 않은 경우 최단거리 조회는 불가능하다.")
    @Test
    void searchShortestPath3() {
        //when
        ExtractableResponse<Response> response = 최단_경로_조회_요청("강남역", "잠실역");

        //then
        최단거리_조회_요청_실패(response);
    }

    /**
     * When 출발역 또는 도착역이 존재하지 않는 역에 대해서 최단거리 조회 요청을 하면
     * Then 요청이 실패한다.
     */
    @DisplayName("출발역과 도착역이 연결되어 있지 않은 경우 최단거리 조회는 불가능하다.")
    @Test
    void searchShortestPath4() {
        //when
        ExtractableResponse<Response> response = 최단_경로_조회_요청("강남역", "부산역");

        //then
        최단거리_조회_요청_실패(response);
    }

    private ExtractableResponse<Response> 최단_경로_조회_요청(String sourceStationName, String targetStationName) {
        Long sourceStationId = stationId(sourceStationName);
        Long targetStationId = stationId(targetStationName);
        return RestAssured.given().log().all().when().get("/paths?source=" + sourceStationId + "&target=" + targetStationId).then().log().all().extract();
    }

    private void 최단거리_조회_응답_확인(final ExtractableResponse<Response> response, final String distance, final List<Long> pathStationIds) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getString("distance")).isEqualTo(distance),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactlyElementsOf(pathStationIds)
        );
    }

    private void 최단거리_조회_요청_실패(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    static Stream<Arguments> generateData() {
        return Stream.of(
                Arguments.of("교대역", "강남역", "10", Arrays.asList("교대역", "강남역")),
                Arguments.of("교대역", "양재역", "5", Arrays.asList("교대역", "남부터미널역", "양재역")),
                Arguments.of("강남역", "양재역", "10", Arrays.asList("강남역", "양재역")),
                Arguments.of("강남역", "남부터미널역", "12", Arrays.asList("강남역", "교대역", "남부터미널역"))
        );
    }

    private Long stationId(String stationName) {
        if ("교대역".equals(stationName)) {
            return 교대역;
        }
        if ("양재역".equals(stationName)) {
            return 양재역;
        }
        if ("강남역".equals(stationName)) {
            return 강남역;
        }
        if ("남부터미널역".equals(stationName)) {
            return 남부터미널역;
        }
        if ("잠실역".equals(stationName)) {
            return 잠실역;
        }

        return 1000000L;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }

    private long 노선_생성_요청(String name, String color, Long upStationId, Long downStationId, int distance) {
        return 지하철_노선_생성_요청(createLineCreateParams(name, color, upStationId, downStationId, distance)).jsonPath().getLong("id");
    }

    private Map<String, String> createLineCreateParams(String name, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", name);
        lineCreateParams.put("color", color);
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", distance + "");
        return lineCreateParams;
    }
}
