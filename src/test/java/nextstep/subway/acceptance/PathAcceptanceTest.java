package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathSteps.경로_조회_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 검색")
public class PathAcceptanceTest extends AcceptanceTest {

    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 삼호선;
    private Long 신분당선;

    /**
     * 교대역      ---     이호선      ---      강남역
     * |                                        |
     * 삼호선                                 신분당선
     * |                                        |
     * 남부터미널역  ---    삼호선      ---      양재역
     *
     * Given 지하철 노선과 지하철역을 생성한다.
     */

    @BeforeEach
    void setup() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

        이호선 = 지하철_노선_생성_요청("이호선", "green", 교대역, 강남역, 10).jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10).jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청("삼호선", "orange", 교대역, 남부터미널역, 2).jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(삼호선, 남부터미널역, 양재역, 3);
    }

    /**
     * When 출발역과 도착역에 대한 경로를 조회한다.
     * Then 출발역에서 도착역까지의 지하철역 경로가 조회된다.
     */
    @DisplayName("출발역에서 도착역까지의 지하철역 경로 조회")
    @Test
    void findPath() {

        ExtractableResponse<Response> 경로_조회_요청_응답 = 경로_조회_요청(교대역, 양재역);

        assertThat(경로_조회_요청_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(경로_조회_요청_응답.jsonPath().getList("stations.name", String.class))
                .containsExactly("교대역", "남부터미널역", "양재역");
        assertThat(경로_조회_요청_응답.jsonPath().getLong("distance")).isEqualTo(5);
    }

    /**
     * When 출발역과 도착역이 같은 경로를 조회한다.
     * Then 오류가 발생한다.
     */
    @DisplayName("출발역과 도착역이 같은 경로를 조회")
    @Test
    void InternalErrorFindPathIfSourceAndTargetEquals() {

        ExtractableResponse<Response> 경로_조회_요청_응답 = 경로_조회_요청(교대역, 교대역);
        assertThat(경로_조회_요청_응답.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * Given 이어지지 않은 지하철 노선을 생성한다.
     * When 이어지지 않은 경로를 조회 요청하면
     * Then 오류가 발생한다.
     */
    @DisplayName("이어지지 않은 경로를 조회")
    @Test
    void InternalErrorFindRoutesIfNotConnectedRoute() {

        // given
        final Long 구로역 = 지하철역_생성_요청("구로역").jsonPath().getLong("id");
        final Long 신도림역 = 지하철역_생성_요청("신도림역").jsonPath().getLong("id");

        final Long 일호선 = 지하철_노선_생성_요청("일호선", "blue", 구로역, 신도림역, 5).jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> 경로_조회_요청_응답 = 경로_조회_요청(강남역, 구로역);

        // then
        assertThat(경로_조회_요청_응답.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * When 존재하지 않은 출발역을 기점으로 경로를 조회하면
     * Then 오류가 발생한다.
     */
    @DisplayName("존재하지 않은 출발역을 기점으로 경로를 조회")
    @Test
    void InternalErrorFindPathIfNotExistSource() {

        final Long 구로디지털단지역 = 지하철역_생성_요청("구로디지털단지역").jsonPath().getLong("id");
        ExtractableResponse<Response> 경로_조회_요청_응답 = 경로_조회_요청(구로디지털단지역, 교대역);
        assertThat(경로_조회_요청_응답.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * When 존재하지 않은 도착역을 기점으로 경로를 조회하면
     * Then 오류가 발생한다.
     */
    @DisplayName("존재하지 않은 도착역을 기점으로 경로를 조회")
    @Test
    void InternalErrorFindPathIfNotExistTarget() {

        final Long 구로디지털단지역 = 지하철역_생성_요청("구로디지털단지역").jsonPath().getLong("id");
        ExtractableResponse<Response> 경로_조회_요청_응답 = 경로_조회_요청(교대역, 구로디지털단지역);
        assertThat(경로_조회_요청_응답.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}

