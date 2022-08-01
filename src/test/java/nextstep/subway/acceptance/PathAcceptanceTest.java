package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.support.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.support.CommonSteps.에러발생_확인;
import static nextstep.subway.acceptance.support.LineSteps.createLineCreateParams;
import static nextstep.subway.acceptance.support.LineSteps.지하철_노선_생성_요청후_식별자_반환;
import static nextstep.subway.acceptance.support.PathSteps.최단경로_조회_요청;
import static nextstep.subway.acceptance.support.SectionsSteps.createSectionCreateParams;
import static nextstep.subway.acceptance.support.SectionsSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.support.StationSteps.지하철역_생성_요청후_식별자_반환;
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
     * Given 지하철역과 지하철 노선에 구간을 생성한다.
     * <p>
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청후_식별자_반환("교대역");
        강남역 = 지하철역_생성_요청후_식별자_반환("강남역");
        양재역 = 지하철역_생성_요청후_식별자_반환("양재역");
        남부터미널역 = 지하철역_생성_요청후_식별자_반환("남부터미널역");

        이호선 = 지하철_노선_생성_요청후_식별자_반환(createLineCreateParams("2호선", "green", 교대역, 강남역, 10));
        신분당선 = 지하철_노선_생성_요청후_식별자_반환(createLineCreateParams("신분당선", "red", 강남역, 양재역, 10));
        삼호선 = 지하철_노선_생성_요청후_식별자_반환(createLineCreateParams("3호선", "orange", 교대역, 남부터미널역, 2));

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    @Nested
    class 최단경로찾기 {
        /**
         * When 서로 다른 지하철역 2개의 최단 경로를 조회하면
         * Then 최단 경로를 조회할 수 있다.
         */
        @Test
        void 성공() {
            // when
            final ExtractableResponse<Response> response = 최단경로_조회_요청(교대역, 양재역);

            // then
            최단경로_순서_확인(response, "교대역", "남부터미널역", "양재역");
        }

        /**
         * When 같은 지하철역 2개의 최단 경로를 조회하면
         * Then 최단 경로를 조회할 수 없다.
         */
        @Test
        void 출발역과_도착역_같은경우() {
            // when
            final ExtractableResponse<Response> response = 최단경로_조회_요청(양재역, 양재역);

            // then
            에러발생_확인(1001, response);
        }

        /**
         * Given 연결되어 있지 않은 지하철역을 생성하고
         * When 서로 연결되어 있지 않은 지하철역 2개의 최단 경로를 조회하면
         * Then 최단 경로를 조회할 수 없다.
         */
        @Test
        void 출발역과_도착역이_연결_되어있지않는경우() {
            // given
            Long 기흥역 = 지하철역_생성_요청후_식별자_반환("기흥역");

            // when
            final ExtractableResponse<Response> response = 최단경로_조회_요청(양재역, 기흥역);

            // then
            에러발생_확인(1001, response);
        }

        /**
         * When 존재하지 않는 지하철역의 최단 경로를 조회하면
         * Then 최단 경로를 조회할 수 없다.
         */
        @Test
        void 존재하지_않는_출발역이나_도착역을_조회할경우() {
            // when
            final ExtractableResponse<Response> response = 최단경로_조회_요청(999L, 9999L);

            // then
            에러발생_확인(1001, response);
        }
    }

    private void 최단경로_순서_확인(final ExtractableResponse<Response> response, String... stationOrder) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.name", String.class)).containsExactly(stationOrder);
    }
}
