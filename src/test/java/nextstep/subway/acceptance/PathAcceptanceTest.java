package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.fixture.LineFixture.createLineCreateParams;
import static nextstep.fixture.SectionFixture.createSectionCreateParams;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathSteps.최단_경로_조회_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("경로 조회 기능")
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

        이호선 = 지하철_노선_생성_요청(createLineCreateParams("2호선", "green", 교대역, 강남역, 10)).jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청(createLineCreateParams("신분당선", "red", 강남역, 양재역, 10)).jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청(createLineCreateParams("3호선", "orange", 교대역, 남부터미널역, 2)).jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }


    /*
    * when 출발역과 도착역의 최단 경로 조회 요청을 하면
    * then 해당 경로의 최단 거리를 알 수 있다
    * */
    @Test
    void 최단_경로를_조회_한다() {
        // when
        ExtractableResponse<Response> response = 최단_경로_조회_요청(교대역, 양재역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getInt("distance")).isEqualTo(5);
    }

    /*
     * when 출발역과 도착역이 같다면
     * then 최단 경로 조회에 실패한다
     * */
    @Test
    void 출발역과_도착역이_같으면_최단_경로_조회에_실패한다() {
        // when
        ExtractableResponse<Response> response = 최단_경로_조회_요청(교대역, 교대역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /*
     * given 새로운 역을 만들고
     * when  출발역과 도착역이 연결되어 있지 않으면
     * then  최단 경로 조회에 실패한다
     * */
    @Test
    void 출발역과_도착역이_연결_되어_있지_않으면_경로_조회에_실패한다() {
        // given
        Long 사당역 = 지하철역_생성_요청("사당역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 최단_경로_조회_요청(교대역, 사당역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /*
     * when  출발역이 존재하지 않는다면
     * then  최단 경로 조회에 실패한다
     * */
    @Test
    void 출발역이_존재하지_않는다면_경로_조회에_실패한다() {
        // when
        ExtractableResponse<Response> response = 최단_경로_조회_요청(100L, 양재역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /*
     * when  도착역이 존재하지 않는다면
     * then  최단 경로 조회에 실패한다
     * */
    @Test
    void 도착역이_존재하지_않는다면_경로_조회에_실패한다() {
        // when
        ExtractableResponse<Response> response = 최단_경로_조회_요청(교대역, 100L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
