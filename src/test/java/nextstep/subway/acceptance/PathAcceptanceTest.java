package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathSteps.최단_경로_조회_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 검색")
class PathAcceptanceTest extends AcceptanceTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * 교대역  10 --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * | 2                      | 10
     * 남부터미널역 3--- *3호선* ---   양재
     * given 역을 네 개 생성 요청하고
     * and 각 역을 묶어 노선으로 추가 요청한 후
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

        이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10);
        신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10);
        삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2);

        지하철_노선에_지하철_구간_생성_요청(삼호선, 남부터미널역, 양재역, 3);
    }

    /**
     * when 최단 경로 조회를 요청하면
     * then 조회 결과가 반환된다.
     */
    @DisplayName("지하철 최단 경로 조회")
    @Test
    public void shortestPath() {
        // when
        ExtractableResponse<Response> response = 최단_경로_조회_요청(강남역, 남부터미널역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.name")).containsExactly("강남역", "교대역", "남부터미널역");
    }

    /**
     * when 출발역과 도착역이 같은 최단 경로 조회를 요청하면
     * then 조회 요청이 실패한다.
     */
    @DisplayName("지하철 최단 경로 조회 실패 - 같은 역간의 경로")
    @Test
    public void sameSourceAndTargetStations() {
        // when
        ExtractableResponse<Response> response = 최단_경로_조회_요청(강남역, 강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * given 연결되어있지 않은 두 개의 노선을 추가 요청한 후
     * when 연결되어있지 않은 역간의 최단 경로 조회를 요청하면
     * then 조회 요청이 실패한다.
     */
    @DisplayName("지하철 최단 경로 조회 실패 - 연결되지 않은 경로")
    @Test
    public void notConnectedPath() {
        // given
        long 홍대역 = 지하철역_생성_요청("홍대역").jsonPath().getLong("id");
        long 합정역 = 지하철역_생성_요청("합정역").jsonPath().getLong("id");
        long 신촌역 = 지하철역_생성_요청("신촌역").jsonPath().getLong("id");
        long 당산역 = 지하철역_생성_요청("당산역").jsonPath().getLong("id");

        지하철_노선_생성_요청("4호선", "black", 홍대역, 합정역, 10);
        지하철_노선_생성_요청("5호선", "white", 신촌역, 당산역, 2);

        // when
        ExtractableResponse<Response> response = 최단_경로_조회_요청(홍대역, 당산역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * when 존재하지 않는 역을 포함한 최단 경로 조회를 요청하면
     * then 조회 요청이 실패한다.
     */
    @DisplayName("지하철 최단 경로 조회 실패 - 존재하지 않는 역")
    @Test
    public void invalidStation() {
        long invalidStationId = 99L;

        // when
        ExtractableResponse<Response> response = 최단_경로_조회_요청(강남역, invalidStationId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
