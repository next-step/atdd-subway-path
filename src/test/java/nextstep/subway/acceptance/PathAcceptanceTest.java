package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathSteps.지하철_경로_조회_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

public class PathAcceptanceTest extends AcceptanceTest {
    private Long 이호선;
    private Long 삼호선;
    private Long 신분당선;

    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
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

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

        이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10).jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10).jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2).jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(삼호선, 남부터미널역, 양재역, 3);
    }

    /**
     * When 출발역과 도착역을 지정해 경로 조회를 하면
     * Then 경로에 있는 역 목록과 조회 경로의 총거리가 나온다.
     */
    @DisplayName("출발역과 도착역으로 경로 조회")
    @Test
    void searchPath() {
        // when
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(교대역, 양재역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id")).containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(response.jsonPath().getObject("distance", Integer.class)).isEqualTo(5);
    }

    /**
     * When 출발역과 도착역이 같은 경로 조회를 하면
     * Then 경로 조회가 실패한다.
     */
    @DisplayName("출발역과 도착역이 같은 경로 조회 실패")
    @Test
    void searchPath_sameSourceAndTarget() {
        // when
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(교대역, 교대역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 출발역과 도착역을 연결하는 경로가 없는 경우
     * Then 경로 조회가 실패한다.
     */
    @DisplayName("출발역과 도착역을 연결하는 경로가 없는 경우 조회 실패")
    @Test
    void searchPath_NotExistPath() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(교대역, 정자역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 출발역이나 도착역이 존재하지 않는 경우
     * Then 경로 조회가 실패한다.
     */
    @DisplayName("출발역과 도착역이 존재하지 않는 경우 조회 실패")
    @Test
    void searchPath_NotExistStations() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(교대역, 정자역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
