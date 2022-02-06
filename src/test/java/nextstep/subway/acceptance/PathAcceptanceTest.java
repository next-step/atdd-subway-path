package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static nextstep.subway.acceptance.steps.LineSteps.지하철_노선_구간_등록을_요청한다;
import static nextstep.subway.acceptance.steps.LineSteps.지하철_노선이_생성되어_있음;
import static nextstep.subway.acceptance.steps.StationSteps.지하철_역_생성_되어있음;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 검색")
class PathAcceptanceTest extends AcceptanceTest {
    private Long 교대역_번호;
    private Long 강남역_번호;
    private Long 양재역_번호;
    private Long 남부터미널역_번호;
    private Long 이호선_번호;
    private Long 신분당선_번호;
    private Long 삼호선_번호;

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

        교대역_번호 = 지하철_역_생성_되어있음("교대역");
        강남역_번호 = 지하철_역_생성_되어있음("강남역");
        양재역_번호 = 지하철_역_생성_되어있음("양재역");
        남부터미널역_번호 = 지하철_역_생성_되어있음("남부터미널역");

        이호선_번호 = 지하철_노선이_생성되어_있음("2호선", "green", 교대역_번호, 강남역_번호, 10);
        신분당선_번호 = 지하철_노선이_생성되어_있음("신분당선", "red", 강남역_번호, 양재역_번호, 10);
        삼호선_번호 = 지하철_노선이_생성되어_있음("3호선", "orange", 교대역_번호, 남부터미널역_번호, 2);

        지하철_노선_구간_등록을_요청한다(삼호선_번호, 남부터미널역_번호, 양재역_번호, 3);
    }

    @DisplayName("출발역과 도착역이 같은 경우의 최단 경로와 거리 조회")
    @Test
    void findShortestPathBySameSourceAndTarget() {
        // when
        final ExtractableResponse<Response> response = 지하철_출발역과_도착역간의_최단_경로_조회를_요청한다(교대역_번호, 교대역_번호);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

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

    @DisplayName("존재하지 않은 출발역을 조회 할 경우의 최단 경로와 거리 조회")
    @Test
    void findShortestPathByNoExistenceSource() {

    }

    @DisplayName("존재하지 않은 도착역을 조회 할 경우의 최단 경로와 거리 조회")
    @Test
    void findShortestPathByNoExistenceTarget() {

    }

    private ExtractableResponse<Response> 지하철_출발역과_도착역간의_최단_경로_조회를_요청한다(final Long source, final Long target) {
        return RestAssured.given().log().all()
                .accept(ContentType.JSON)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(String.format("/paths?source=%s&target=%s", source, target))
                .then().log().all()
                .extract();
    }
}
