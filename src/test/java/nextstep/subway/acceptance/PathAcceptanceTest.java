package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.LineSectionAcceptanceTest.createSectionCreateParams;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathSteps.지하철_최단_거리_조회_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
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

        이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10).jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10).jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2).jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    /**
     * When 경로를 조회하면
     * Then 최단 경로 역 목록과 구간의 거리를 찾을 수 있다.
     */
    @Test
    void 최단_거리를_구한다() {
        // when
        var 최단_거리_응답 = 지하철_최단_거리_조회_요청(교대역, 양재역);

        // then
        지하철_역이_포함되어_있다(최단_거리_응답, 교대역, 남부터미널역, 양재역);
        최단_거리를_확인한다(최단_거리_응답, 5);
    }


    /**
     * When 출발역과 도착역이 같은 경우
     * Then 최단 경로 조회에 실패한다.
     */
    @Test
    void 출발역과_도착역이_같은_경우_예외를_일으킨다() {
        // when
        var 최단_거리_응답 = 지하철_최단_거리_조회_요청(교대역, 교대역);

        // then
        최단_거리를_구하는데_실패한다(최단_거리_응답);
    }

    /**
     * Given 새로운 노선을 만든다.
     * When 새로운 노선역이 출발역과 도착역이 연결되어 있지 않은 경우
     * Then 최단 경로 조회에 실패한다.
     */
    @Test
    void 출발역과_도착역이_연결되지_않은_경우_예외를_일으킨다() {
        // given
        var 신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");
        var 고속터미널역 = 지하철역_생성_요청("고속터미널역").jsonPath().getLong("id");
        var 구호선 = 지하철_노선_생성_요청("구호선", "gold", 신논현역, 고속터미널역, 10).jsonPath().getLong("id");

        // when
        var 최단_거리_응답 = 지하철_최단_거리_조회_요청(신논현역, 강남역);

        // then
        최단_거리를_구하는데_실패한다(최단_거리_응답);
    }

    /**
     * When 존재하지 않은 출발역이나 도착역을 조회 할 경우
     * Then 최단 경로 조회에 실패한다.
     */

    private void 지하철_역이_포함되어_있다(ExtractableResponse<Response> 지하철_노선_목록_조회_응답, Long... elements) {
        assertThat(지하철_노선_목록_조회_응답.jsonPath().getList("stations.id", Long.class)).containsExactly(elements);
    }

    private void 최단_거리를_확인한다(ExtractableResponse<Response> 지하철_노선_목록_조회_응답, int distance) {
        assertThat(지하철_노선_목록_조회_응답.jsonPath().getInt("distance")).isEqualTo(distance);
    }

    private void 최단_거리를_구하는데_실패한다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
