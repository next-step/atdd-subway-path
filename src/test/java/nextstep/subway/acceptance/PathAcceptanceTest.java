package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

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
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */

    /**
     * Given 지하철 역과 노선을 생성하고 구간을 등록한 뒤
     */
    @BeforeEach
    void setUp() {
        교대역 = createStation("교대역");
        강남역 = createStation("강남역");
        양재역 = createStation("양재역");
        남부터미널역 = createStation("남부터미널역");

        이호선 = createLine("2호선", "green", 교대역, 강남역, 10L);
        신분당선 = createLine("신분당선", "red", 강남역, 양재역, 10L);
        삼호선 = createLine("3호선", "orange", 교대역, 남부터미널역, 2L);

        SectionSteps.addSection(삼호선, 남부터미널역, 양재역, 3L);
    }

    /**
     * When 출발역과 도착역으로 경로를 조회하면
     * Then 출발역으로부터 도착역까지의 경로에 있는 역 목록과 조회한 경로 구간의 거리가 조회된다.
     */
    @DisplayName("지하철 노선 경로를 조회한다.")
    @Test
    void getPath() {
        // when
        ExtractableResponse<Response> response = PathSteps.getPath(교대역, 양재역);
        List<Long> ids = getStations(response);
        Long distance = getDistance(response);

        // then
        assertThat(ids).containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(distance).isEqualTo(5);
    }

    private static long getDistance(ExtractableResponse<Response> response) {
        return response.jsonPath().getLong("distance");
    }

    private static List<Long> getStations(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("stations", StationResponse.class)
                .stream().map(StationResponse::getId).collect(Collectors.toList());
    }

    private static Long createStation(String name) {
        return StationSteps.createStation(name).jsonPath().getLong("id");
    }

    private static Long createLine(String name, String color, Long upStation, Long downStation, Long distance) {
        return LineSteps.createLine(name, color, upStation, downStation, distance).jsonPath().getLong("id");
    }

}
