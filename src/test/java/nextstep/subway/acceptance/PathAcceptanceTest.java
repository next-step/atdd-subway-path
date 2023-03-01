package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재역
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
     *  Given 새로운 역, 구간, 노선을 추가하고
     *  When 출발역과 도착역을 설정하여 구간 경로 조회 요청을하면
     *  Then 최단 거리의 구간에 해당하는 역들과 거리 값을 알 수 있다.
     */
    @DisplayName("출발역과 도착역을 설정하면 최단 경로를 조회할 수 있다.")
    @Test
    public void findPath() {
        // when
        ExtractableResponse<Response> response = 거리_경로_조회_요청(교대역, 양재역);

        // then
        assertAll(
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(교대역, 남부터미널역, 양재역),
                () -> assertThat(response.jsonPath().getInt("distance")).isEqualTo(5)
        );
    }

    /**
     *  Given 새로운 역, 구간, 노선을 추가하고
     *  When 존재하지 않는 출발역 혹은 도착역으로 경로 조회 요청하면
     *  Then 경로를 조회할 수 없다.
     */
    @DisplayName("존재하지 않는 출발역과 도착역으로 최단 경로를 조회할 수 없다.")
    @Test
    public void findPathStationNotExistException() {
        // when
        ExtractableResponse<Response> sourceExceptionResponse = 거리_경로_조회_요청(5L, 양재역);
        ExtractableResponse<Response> targetExceptionResponse = 거리_경로_조회_요청(교대역, 5L);

        // then
        assertAll(
                () -> assertThat(sourceExceptionResponse.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                () -> assertThat(targetExceptionResponse.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value())
        );
    }
}
