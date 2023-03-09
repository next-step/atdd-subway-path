package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.createSectionCreateParams;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathSteps.지하철_경로_조회_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 경로 검색")
class PathsAcceptanceTest extends AcceptanceTest {

    private Long 교대역_ID;
    private Long 강남역_ID;
    private Long 양재역_ID;
    private Long 남부터미널역_ID;
    private Long 이호선_ID;
    private Long 신분당선_ID;
    private Long 삼호선_ID;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                         |
     * *3호선*                   *신분당선*
     * |                         |
     * 남부터미널역  --- *3호선* ---   양재역
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역_ID = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역_ID = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역_ID = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역_ID = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

        이호선_ID = 지하철_노선_생성_요청("2호선", "green", 교대역_ID, 강남역_ID, 10);
        신분당선_ID = 지하철_노선_생성_요청("신분당선", "red", 강남역_ID, 양재역_ID, 10);
        삼호선_ID = 지하철_노선_생성_요청("3호선", "orange", 교대역_ID, 남부터미널역_ID, 2);

        지하철_노선에_지하철_구간_생성_요청(삼호선_ID,
            createSectionCreateParams(남부터미널역_ID, 양재역_ID, 3));
    }

    @DisplayName("지하철 경로 검색")
    @Test
    void getPath() {
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(교대역_ID, 양재역_ID);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id"))
            .containsExactly(교대역_ID.intValue(), 남부터미널역_ID.intValue(), 양재역_ID.intValue());
        assertThat(response.jsonPath().getInt("distance"))
            .isEqualTo(5);
    }

    @DisplayName("지하철 경로 검색 실패 - 출발역과 도착역 같음")
    @Test
    void getPath_SourceIsEqualToTarget() {
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(교대역_ID, 교대역_ID);

        실패함(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("지하철 경로 검색 실패 - 출발역과 도착역이 연결되어있지 않음")
    @Test
    public void getPath_SourceDoesNotLinkedWithTarget() {
        Long 서울숲역_ID = 지하철역_생성_요청("서울숲역").jsonPath().getLong("id");

        ExtractableResponse<Response> response = 지하철_경로_조회_요청(교대역_ID, 서울숲역_ID);

        실패함(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("지하철 경로 검색 실패 - 존재하지 않는 출발역")
    @Test
    public void getPath_SourceDoesNotExist() {
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(0L, 양재역_ID);

        실패함(response, HttpStatus.NOT_FOUND);
    }

    @DisplayName("지하철 경로 검색 실패 - 존재하지 않는 도착역")
    @Test
    public void getPath_TargetDoesNotExist() {
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(교대역_ID, 0L);

        실패함(response, HttpStatus.NOT_FOUND);
    }

}
