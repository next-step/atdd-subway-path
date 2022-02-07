package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.LineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathSteps.지하철_경로조회;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;

@DisplayName("지하철 경로 검색")
class PathAcceptanceTest extends AcceptanceTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 명동역;
    private Long 충무로역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;
    private Long 사호선;

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
        명동역 = 지하철역_생성_요청("명동역").jsonPath().getLong("id");
        충무로역 = 지하철역_생성_요청("충무로역").jsonPath().getLong("id");

        이호선 = 지하철_노선_생성_요청(new LineRequest("2호선", "green", 교대역, 강남역, 10)).jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청(new LineRequest("신분당선", "red", 강남역, 양재역, 10)).jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청(new LineRequest("3호선", "orange", 교대역, 남부터미널역, 2)).jsonPath().getLong("id");
        사호선 = 지하철_노선_생성_요청(new LineRequest("4호선", "blue", 명동역, 충무로역, 4)).jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(삼호선, LineSteps.createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    /**
     * When 지하철 경로 조회를 하면
     * Then 지하철 경로를 응답받는다
     */
    @DisplayName("지하철 경로 조회")
    @Test
    void getPaths() {
        // when
        ExtractableResponse<Response> response = 지하철_경로조회(교대역, 양재역);

        // then
        인수테스트_요청_응답을_확인한다(response, HttpStatus.OK);
    }

    /**
     * Given 출발역과 도착역을 동일하게 설정하고
     * When 지하철 경로를 조회하면
     * Then 에러가 발생한다.
     */
    @DisplayName("출발역과 도착역을 같게 설정하고 경로 찾기")
    @Test
    void duplicateStation() {
        // when
        ExtractableResponse<Response> response = 지하철_경로조회(교대역, 교대역);

        // then
        인수테스트_요청_응답을_확인한다(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Given 존재하지 않은 출발역이나 도착역을 설정하고
     * When 지하철 경로를 조회하면
     * Then 에러가 발생한다.
     */
    @DisplayName("존재하지 않은 출발역이나 도착역을 조회 할 경우")
    @Test
    void notExistStationPath() {
        // when
        long 선릉역 = 지하철역_생성_요청("선릉역").jsonPath().getLong("id");

        ExtractableResponse<Response> response = 지하철_경로조회(교대역, 선릉역);

        // then
        인수테스트_요청_응답을_확인한다(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DisplayName("출발역과 도착역이 연결되지 않은 경우")
    @Test
    void notConnectedPath() {
        // when
        ExtractableResponse<Response> response = 지하철_경로조회(교대역, 명동역);

        // then
        인수테스트_요청_응답을_확인한다(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
