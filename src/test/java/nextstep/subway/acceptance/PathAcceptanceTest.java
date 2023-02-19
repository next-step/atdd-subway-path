package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathSteps.최단_경로_조회;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 조회")
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

        이호선 = 지하철_노선_생성_요청("2호선", "green").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(이호선, createSectionCreateParams(교대역, 강남역, 10));

        신분당선 = 지하철_노선_생성_요청("신분당선", "red").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 10));

        삼호선 = 지하철_노선_생성_요청("3호선", "orange").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(교대역, 남부터미널역, 2));
        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }

    @DisplayName("출발역과 도착역이 같은경우 예외발생")
    @Test
    void exception_when_departure_station_and_destination_station_are_same() {
        // When
        ExtractableResponse<Response> response = 최단_경로_조회(강남역, 강남역);

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우 예외발생")
    @Test
    void exception_when_departure_station_and_destination_station_are_not_reachable() {
        // Given
        Long 부산역 = 지하철역_생성_요청("부산역").jsonPath().getLong("id");
        Long 강원역 = 지하철역_생성_요청("강원역").jsonPath().getLong("id");

        Long 구호선 = 지하철_노선_생성_요청("구호선", "gold").jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(구호선, createSectionCreateParams(부산역, 강원역, 10));

        // When
        ExtractableResponse<Response> response = 최단_경로_조회(강남역, 부산역);

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("존재하지 않은 출발역이나 도착역을 조회 할 경우 예외발생")
    @Test
    void exception_when_not_exist_station() {
        // Given
        List<Long> 존재하는역들 = List.of(교대역, 강남역, 양재역, 남부터미널역);

        Random randomGenerator = new Random();
        long 존재하지_않는_역 = randomGenerator.longs()
                .filter(id -> !존재하는역들.contains(id))
                .findFirst()
                .getAsLong();

        // When
        ExtractableResponse<Response> response = 최단_경로_조회(강남역, 존재하지_않는_역);

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("출발역과 도착역의 최단거리를 조회한다")
    @Test
    public void find_shortest_path_when_same_line() {
        // When
        ExtractableResponse<Response> response = 최단_경로_조회(교대역, 양재역);

        // Then
        assertThat(response.jsonPath().getList("stations.name", String.class)).containsExactly("교대역", "남부터미널역", "양재역");
        assertThat(response.jsonPath().getLong("distance")).isEqualTo(5);
    }

    @DisplayName("출발역과 도착역의 최단거리를 조회한다")
    @Test
    public void find_shortest_path_when_transfer_different_line() {
        // When
        ExtractableResponse<Response> response = 최단_경로_조회(강남역, 남부터미널역);

        // Then
        assertThat(response.jsonPath().getList("stations.name", String.class)).containsExactly("강남역", "교대역", "남부터미널역");
        assertThat(response.jsonPath().getLong("distance")).isEqualTo(12);
    }
}
