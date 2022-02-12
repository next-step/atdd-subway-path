package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static nextstep.subway.utils.RestTestUtils.응답ID추출;
import static org.assertj.core.api.Assertions.assertThat;

public class PathAcceptanceTest extends AcceptanceTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * 교대역    --- *2호선* (10 km) ---      강남역
     * |                                        |
     * *3호선* (2 km)                       *신분당선*  (10 km)
     * |                                        |
     * 남부터미널역  --- *3호선* (3 km) ---   양재
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 응답ID추출(지하철역_생성_요청("교대역"));
        강남역 = 응답ID추출(지하철역_생성_요청("강남역"));
        양재역 = 응답ID추출(지하철역_생성_요청("양재역"));
        남부터미널역 = 응답ID추출(지하철역_생성_요청("남부터미널역"));

        이호선 = 응답ID추출(지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10));
        신분당선 = 응답ID추출(지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10));
        삼호선 = 응답ID추출(지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2));

        지하철_노선에_지하철_구간_생성_요청(삼호선, 구간생성_요청_파라미터(남부터미널역, 양재역, 3));
    }

    /**
     * Given 지하철 노선들에 구간들을 생성하고
     * When 출발역과 도착역을 기반으로 경로 조회를 요청하면
     * Then 최단경로 지하철역 목록과 최단거리를 응답받는다
     */
    @DisplayName("출발역과 도착역의 최단 경로 조회")
    @Test
    void findShortestPath() {
        // given
        Long 출발역 = 교대역;
        Long 도착역 = 양재역;

        // when
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(출발역, 도착역);

        // then
        List<String> responseStations = response.jsonPath().getList("stations.name", String.class);
        int responseDistance = response.jsonPath().getInt("distance");
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseStations).containsExactly("교대역", "남부터미널역", "양재역");
        assertThat(responseDistance).isEqualTo(5);
    }

    /**
     * Given 지하철 노선들에 구간들을 생성하고
     * When 존재하지 않는 출발역 혹은 도착역을 기반으로 경로 조회를 요청하면
     * Then 경로 조회 요청이 실패한다
     */
    @DisplayName("등록되지 않은 출발역과 도착역의 최단 경로 조회")
    @ParameterizedTest
    @CsvSource(value = {
            "1, 9999",
            "9999, 1"
    })
    void findShortestPathNotFoundStationsFailure(Long 출발역, Long 도착역) {
        // given

        // when
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(출발역, 도착역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선들에 구간들을 생성하고
     * Given 어떠한 노선에도 등록되지 않을 새로운 지하철 역을 생성하고
     * When 연결되어 있지 않은 출발역과 도착역을 기반으로 경로 조회를 요청하면
     * Then 경로 조회 요청이 실패한다
     */
    @DisplayName("연결되어 있지 않은 출발역과 도착역의 최단 경로 조회")
    @Test
    void findShortestPathNotConnectedStationsFailure() {
        // given
        Long 출발역 = 응답ID추출(지하철역_생성_요청("몽촌토성역"));
        Long 도착역 = 강남역;

        // when
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(출발역, 도착역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선들에 구간들을 생성하고
     * Given 노선에는 등록되어 있지만 연결되지 않을 새로운 지하철 역을 생성하고
     * Given 새로운 노선을 생성하고
     * When 연결되어 있지 않은 출발역과 도착역을 기반으로 경로 조회를 요청하면
     * Then 경로 조회 요청이 실패한다
     */
    @DisplayName("연결되어 있지 않은 출발역과 도착역의 최단 경로 조회")
    @Test
    void findShortestPathNotConnectedStationsByLineFailure() {
        // given
        Long 몽촌토성역 = 응답ID추출(지하철역_생성_요청("몽촌토성역"));
        Long 잠실역 = 응답ID추출(지하철역_생성_요청("잠실역"));
        이호선 = 응답ID추출(지하철_노선_생성_요청("8호선", "pink", 몽촌토성역, 잠실역, 10));

        Long 출발역 = 몽촌토성역;
        Long 도착역 = 강남역;

        // when
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(출발역, 도착역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
