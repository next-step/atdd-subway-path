package nextstep.subway.acceptance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.PathFinderSteps.지하철_경로_조회;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 경로 조회 기능")
public class PathFinderAcceptanceTest extends AcceptanceTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 상도역;
    private Long 장승배기역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;
    private Long 칠호선;

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
        상도역 = 지하철역_생성_요청("상도역").jsonPath().getLong("id");
        장승배기역 = 지하철역_생성_요청("장승배기역").jsonPath().getLong("id");

        이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10);
        신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10);
        삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2);
        칠호선 = 지하철_노선_생성_요청("7호선", "blue", 상도역, 장승배기역, 4);

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    /**
     * When 출발역과 도착역의 경로를 조회하면
     * Then 출발역부터 도착역까지 최단거리 경로에 있는 역 목록을 조회할 수 있다.
     */
    @Test
    @DisplayName("최단거리 경로를 조회한다.")
    void findPath() {
        // when
        var response = 지하철_경로_조회(교대역, 양재역);

        // then
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.jsonPath().getInt("distance")).isEqualTo(5);
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(교대역, 남부터미널역, 양재역);
        });
    }

    /**
     * When 출발역과 도착역이 같은 경로를 조회하면
     * Then 예외가 발생한다.
     */
    @Test
    @DisplayName("출발역과 도착역이 같은역에 대한 경로 조회")
    void findPath_WithSameDepartureAndArrivalStation() {
        // when
        var response = 지하철_경로_조회(교대역, 교대역);

        // then
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        });
    }

    /**
     * When 출발역과 도착역이 연결되어있지 않은 경로를 조회하면
     * Then 예외가 발생한다.
     */
    @Test
    @DisplayName("연결되어있지 않은 경로 조회")
    void findPath_WithUnConnectedStation() {
        // when
        var response = 지하철_경로_조회(강남역, 상도역);

        // then
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        });
    }

    /**
     * When 존재하지 않는 출발역이나 도착역을 조회하면
     * Then 예외가 발생한다.
     */
    @Test
    @DisplayName("존재하지 않는 역을 조회")
    void findPath_WithNotExistsStation() {
        // given
        final Long 건대입구역 = 98L;
        final Long 뚝섬유원지역 = 99L;

        // when
        var response = 지하철_경로_조회(건대입구역, 뚝섬유원지역);

        // then
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        });
    }
}
