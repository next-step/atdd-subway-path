package nextstep.subway.acceptance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathSteps.경로_조회_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class PathAcceptanceTest extends AcceptanceTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 잠실역;
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
        잠실역 = 지하철역_생성_요청("잠실역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

        이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10).jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10).jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2).jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 잠실역, 10));
    }

    /**
     * When 같은 노선에 있는 두 지하철역 사이의 경로를 조회하면
     * Then 경로 상에 있는 모든 지하철역을 응답받는다.
     */
    @DisplayName("같은 노선에 있는 두 지하철역 사이의 경로를 조회한다.")
    @Test
    void findPath() {
        List<Long> 정방향_경로 = 경로_조회_요청(교대역, 양재역).jsonPath().getList("stations.id", Long.class);
        List<Long> 역방향_경료 = 경로_조회_요청(양재역, 교대역).jsonPath().getList("stations.id", Long.class);

        assertAll(
                () -> assertThat(정방향_경로).containsExactly(교대역, 남부터미널역, 양재역),
                () -> assertThat(역방향_경료).containsExactly(양재역, 남부터미널역, 교대역)
        );
    }

    /**
     * When 다른 노선에 있는 두 지하철역 사이의 경로를 조회하면
     * Then 경로 상에 있는 모든 지하철역을 응답받는다.
     */
    @DisplayName("환승해야 하는 두 지하철역 사이의 경로를 조회한다.")
    @Test
    void findTransferPath() {
        List<Long> 정방향_경로 = 경로_조회_요청(교대역, 잠실역).jsonPath().getList("stations.id", Long.class);
        List<Long> 역방향_경료 = 경로_조회_요청(잠실역, 교대역).jsonPath().getList("stations.id", Long.class);

        assertAll(
                () -> assertThat(정방향_경로).containsExactly(교대역, 남부터미널역, 양재역, 잠실역),
                () -> assertThat(역방향_경료).containsExactly(잠실역, 양재역, 남부터미널역, 교대역)
        );
    }

    /**
     * When 출발역과 도착역이 같은 경우에 경로를 조회하면
     * Then INTERNAL_SERVER_ERROR가 발생한다.
     */
    @DisplayName("출발역, 도착역이 같은 경우 경로를 조회할 수 없다.")
    @Test
    void findPathSameStations() {
        assertThat(경로_조회_요청(교대역, 교대역).statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * When 도달할 수 없는 역으로의 경로를 조회하면
     * Then INTERNAL_SERVER_ERROR가 발생한다.
     */
    @DisplayName("도달할 수 없는 역으로의 경로를 조회할 수 없다.")
    @Test
    void findPathToUnreachableStation() {
        Long 잠실역 = 지하철역_생성_요청("잠실역").jsonPath().getLong("id");
        Long 선릉역 = 지하철역_생성_요청("선릉역").jsonPath().getLong("id");
        지하철_노선_생성_요청("4호선", "yellow", 잠실역, 선릉역, 5).jsonPath().getLong("id");

        assertThat(경로_조회_요청(교대역, 선릉역).statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }


    /**
     * When 존재하지 않는 역으로의 경로를 조회하면
     * Then INTERNAL_SERVER_ERROR가 발생한다.
     */
    @DisplayName("존재하지 않는 역으로의 경로를 조회할 수 없다.")
    @Test
    void findPathToNonExistentStations() {
        assertThat(경로_조회_요청(교대역, 100L).statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
