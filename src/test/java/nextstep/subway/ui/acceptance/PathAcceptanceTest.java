package nextstep.subway.ui.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.ui.acceptance.LineSteps.*;
import static nextstep.subway.ui.acceptance.PathSteps.경로_탐색_요청;
import static nextstep.subway.ui.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 검색")
class PathAcceptanceTest extends AcceptanceTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 수원역;
    private Long 망포역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;
    private Long 분당선;


    /**
     * Given 다음 그림과 같이 지하철역, 노선, 구간이 등록되어 있고
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     *
     * 수원역
     * 망포역
     */
    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");
        수원역 = 지하철역_생성_요청("수원역").jsonPath().getLong("id");
        망포역 = 지하철역_생성_요청("망포역").jsonPath().getLong("id");

        이호선 = 지하철_노선_생성_요청(createLineCreateParams("2호선", "green", 교대역, 강남역, 10)).jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청(createLineCreateParams("신분당선", "red", 강남역, 양재역, 10)).jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청(createLineCreateParams("3호선", "orange", 교대역, 남부터미널역, 2)).jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    /**
     * When 출발역부터 도착역까지의 최단 경로를 조회하면
     * Then 역 목록과 거리가 반환된다
     */
    @Test
    @DisplayName("지하철 경로를 조회한다.")
    void findPath() {
        //given
        //when
        ExtractableResponse<Response> findPathResponse = 경로_탐색_요청(교대역, 양재역);
        //then
        assertThat(findPathResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findPathResponse.jsonPath().getList("stations.id", Long.class)).containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(findPathResponse.jsonPath().getInt("distance")).isEqualTo(5);
    }

    /**
     * Given 출발역과 도착역을 동일하게 설정하고
     * When 경로를 조회하면
     * Then 경로 조회를 실패한다
     */
    @Test
    @DisplayName("출발역과 도착역을 동일하게 설정하여 경로를 조회하면 실패한다.")
    void findPathSetSameSourceAndTarget() {
        //given
        //when
        ExtractableResponse<Response> findPathResponse = 경로_탐색_요청(양재역, 양재역);
        //then
        assertThat(findPathResponse.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * Given 기존 노선과 연결되지 않은 신규 노선과 구간을 추가하고
     * When 출발역부터 도착역까지의 구간이 연결되지 않은 경로를 조회하면
     * Then 경로 조회를 실패한다
     */
    @Test
    @DisplayName("출발역부터 도착역까지의 구간이 연결되지 않은 경로를 조회하면 실패한다.")
    void findPathSetNotConnectedPath() {
        //given
        Long 분당선 = 지하철_노선_생성_요청(createLineCreateParams("분당선", "yellow", 수원역, 망포역, 5)).jsonPath().getLong("id");
        //when
        ExtractableResponse<Response> findPathResponse = 경로_탐색_요청(교대역, 수원역);
        //then
        assertThat(findPathResponse.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * Given 출발역, 도착역 둘중 하나를 노선에 포함되지 않은 역으로 설정하고
     * When 경로를 조회하면
     * Then 경로 조회를 실패한다
     */
    @Test
    @DisplayName("노선에 포함되지 않은 역을 출발역, 도착역으로 설정하고 경로를 조회하면 실패한다.")
    void findPathSetNotFoundStation() {
        //given
        //when
        ExtractableResponse<Response> findPathResponse = 경로_탐색_요청(교대역, 수원역);
        //then
        assertThat(findPathResponse.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
