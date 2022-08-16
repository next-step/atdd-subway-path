package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathSteps.경로_조회_요청;
import static nextstep.subway.acceptance.SectionAcceptanceTest.createSectionCreateParams;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

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

        이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10);
        신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10);
        삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2);

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    /**
     * When 출발역과 도착역이 같은 호선으로 조회하면
     * Then 경로에 있는 역 목록과 거리를 정상 조회한다.
     */
    @DisplayName("출발역과 도착역이 같은 호선으로 경로를 조회한다.")
    @Test
    void startAndEndStationSameLineFindSuccess() {
        // when 2호선 라인의 역 경로 조회를 요청한다.
        ExtractableResponse<Response> response = 경로_조회_요청(교대역, 강남역);

        // then 2호선 라인의 역 목록과 거리를 정상 조회한다.
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(교대역, 강남역),
                () -> assertThat(response.jsonPath().getInt("distance")).isEqualTo(10)
        );
    }

    /**
     * When 출발역과 다른 호선을 도착역으로 경로를 조회하면
     * Then 경로에 있는 역 목록과 거리를 정상 조회한다.
     */
    @DisplayName("출발역과 도착역이 다른 호선으로 경로를 조회한다.")
    @Test
    void startAndEndStationNotSameLineFindSuccess() {
        // when 다른 호선으로 환승을 하도록 경로 조회한다.
        ExtractableResponse<Response> response = 경로_조회_요청(교대역, 양재역);

        // then 경로 내에 포함되는 역 목록과 거리를 정상조회한다.
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(교대역, 남부터미널역,
                        양재역),
                () -> assertThat(response.jsonPath().getInt("distance")).isEqualTo(5)
        );
    }

    /**
     * When 출발역과 도착역이 같은 경우
     * Then 경로조회에 실패한다.
     */
    @DisplayName("출발역과 도착역이 같은 경우 경로 조회에 실패한다.")
    @Test
    void sameStartAndEndStationFindLineFail() {
        // when 출발역과 도착역이 같도록 조회한다.
        ExtractableResponse<Response> response = 경로_조회_요청(교대역, 교대역);

        // then 경로 조회결과 실패한다.
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * Given 연결되지 않은 지하철 노선을 추가하고
     * When 연결되지 않은 역끼리 경로를 조회하면
     * Then 경로조회에 실패한다.
     */
    @DisplayName("출발역과 도착역이 연결되지 않은 경우 경로 조회에 실패한다.")
    @Test
    void startAndEndStationIsNotLinkFindLineFail() {
        // given 연결되지 않은 지하철 노선 추가 생성한다.
        Long 신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");
        Long 언주역 = 지하철역_생성_요청("언주역").jsonPath().getLong("id");
        지하철_노선_생성_요청("9호선", "brown", 신논현역, 언주역, 10);

        // when 연결되지 않은 역의 경로를 조회한다
        ExtractableResponse<Response> response = 경로_조회_요청(신논현역, 교대역);

        // then 경로조회에 실패한다.
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * When 존재하지 않는 역의 경로를 조회하면
     * Then 경로조회에 실패한다.
     */
    @DisplayName("존재하지 않는 출발역이나 도착역을 조회하는 경우 조회 실패한다.")
    @Test
    void startOrEndStationNotExistFindLineFail() {
        // when 존재하지 않는 역으로 경로 조회한다.
        ExtractableResponse<Response> response = 경로_조회_요청(교대역, 99L);

        // then 경로조회에 실패한다.
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
