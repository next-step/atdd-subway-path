package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.steps.LineSteps.지하철_노선_구간_등록을_요청한다;
import static nextstep.subway.acceptance.steps.LineSteps.지하철_노선이_생성되어_있음;
import static nextstep.subway.acceptance.steps.PathSteps.지하철_출발역과_도착역간의_최단_경로_조회를_요청한다;
import static nextstep.subway.acceptance.steps.StationSteps.지하철_역_생성_되어있음;
import static nextstep.subway.fixture.LineFixture.*;
import static nextstep.subway.fixture.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 경로 검색")
class PathAcceptanceTest extends AcceptanceTest {

    private Long 교대역_번호;
    private Long 강남역_번호;
    private Long 양재역_번호;
    private Long 남부터미널역_번호;
    private Long 이호선_번호;
    private Long 신분당선_번호;
    private Long 삼호선_번호;
    private Long 존재하지_않는_역_번호;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */

    /**
     * Given 지하철 노선 생성을 요청 하고
     * And 지하철 노선에 새로운 구간을 등록하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역_번호 = 지하철_역_생성_되어있음(교대역);
        강남역_번호 = 지하철_역_생성_되어있음(강남역);
        양재역_번호 = 지하철_역_생성_되어있음(양재역);
        남부터미널역_번호 = 지하철_역_생성_되어있음(남부터미널역);
        존재하지_않는_역_번호 = -1L;

        이호선_번호 = 지하철_노선이_생성되어_있음(이호선, 초록색, 교대역_번호, 강남역_번호, 교대_강남_거리);
        신분당선_번호 = 지하철_노선이_생성되어_있음(신분당선, 빨강색, 강남역_번호, 양재역_번호, 강남_양재_거리);
        삼호선_번호 = 지하철_노선이_생성되어_있음(삼호선, 오렌지색, 교대역_번호, 남부터미널역_번호, 교대_남부터미널_거리);

        지하철_노선_구간_등록을_요청한다(삼호선_번호, 남부터미널역_번호, 양재역_번호, 남부터미널_양재_거리);
    }

    /***
     * When 출발역과 도착역간의 최단 경로 찾기 요청을 하면
     * Then 출발역과 도착역간의 최단 경로와 거리를 조회할 수 있다.
     */
    @DisplayName("출발역과 도착역간의 최단 경로와 거리 조회")
    @Test
    void findShortestPath() {
        // when
        final ExtractableResponse<Response> response = 지하철_출발역과_도착역간의_최단_경로_조회를_요청한다(교대역_번호, 양재역_번호);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(교대역_번호, 남부터미널역_번호, 양재역_번호)
        );
    }

    /**
     * When 출발역과 도착역이 같은 경우의 최단 경로 찾기 요청을 하면
     * Then 출발역과 도착역간의 최단 경로와 거리를 조회할 수 없다.
     */
    @DisplayName("출발역과 도착역이 같은 경우의 최단 경로와 거리 조회")
    @Test
    void findShortestPathBySameSourceAndTarget() {
        // when
        final ExtractableResponse<Response> response = 지하철_출발역과_도착역간의_최단_경로_조회를_요청한다(교대역_번호, 교대역_번호);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 출발역과 도착역이 연결이 되어 있지 않은 경우의 최단 경로 찾기 요청을 하면
     * Then 출발역과 도착역간의 최단 경로와 거리를 조회할 수 없다.
     */
    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우의 최단 경로와 거리 조회")
    @Test
    void findShortestPathByNoLinkedSourceAndTarget() {
        // given
        final Long 광명역_번호 = 지하철_역_생성_되어있음("광명역");

        // when
        final ExtractableResponse<Response> response = 지하철_출발역과_도착역간의_최단_경로_조회를_요청한다(교대역_번호, 광명역_번호);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 존재하지 않은 출발역을 조회 할 경우의 최단 경로 찾기 요청을 하면
     * Then 출발역과 도착역간의 최단 경로와 거리를 조회할 수 없다.
     */
    @DisplayName("존재하지 않은 출발역을 조회 할 경우의 최단 경로와 거리 조회")
    @Test
    void findShortestPathByNoExistenceSource() {
        // when
        final ExtractableResponse<Response> response = 지하철_출발역과_도착역간의_최단_경로_조회를_요청한다(존재하지_않는_역_번호, 교대역_번호);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /***
     * When 존재하지 않은 도착역을 조회 할 경우의 최단 경로 찾기 요청을 하면
     * Then 출발역과 도착역간의 최단 경로와 거리를 조회할 수 없다.
     */
    @DisplayName("존재하지 않은 도착역을 조회 할 경우의 최단 경로와 거리 조회")
    @Test
    void findShortestPathByNoExistenceTarget() {
        // when
        final ExtractableResponse<Response> response = 지하철_출발역과_도착역간의_최단_경로_조회를_요청한다(교대역_번호, 존재하지_않는_역_번호);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
