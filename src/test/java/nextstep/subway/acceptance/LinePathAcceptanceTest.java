package nextstep.subway.acceptance;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.acceptance.CommonSteps.getIdFromResponse;
import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.PathSteps.createPathReadParams;
import static nextstep.subway.acceptance.PathSteps.지하철_경로_조회;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 경로 조회 기능")
public class LinePathAcceptanceTest extends AcceptanceTest {

    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;

    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    private int 교대역_강남역 = 10;
    private int 강남역_양재역 = 10;
    private int 교대역_남부터미널역 = 2;
    private int 남부터미널역_양재역 = 19;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재역
     *
     * Given 지하철 역과 노선 및 구간 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = getIdFromResponse(지하철역_생성_요청("교대역"));
        강남역 = getIdFromResponse(지하철역_생성_요청("강남역"));
        양재역 = getIdFromResponse(지하철역_생성_요청("양재역"));
        남부터미널역 = getIdFromResponse(지하철역_생성_요청("남부터미널역"));

        이호선 = getIdFromResponse(지하철_노선_생성_요청(createLineCreateParams("2호선", "green", 교대역, 강남역, 교대역_강남역)));
        신분당선 = getIdFromResponse(지하철_노선_생성_요청(createLineCreateParams("신분당선", "red", 강남역, 양재역, 강남역_양재역)));
        삼호선 = getIdFromResponse(지하철_노선_생성_요청(createLineCreateParams("3호선", "orange", 교대역, 남부터미널역, 교대역_남부터미널역)));

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 남부터미널역_양재역));
    }

    /**
     * When 출발 역과 도착 역으로 최단 경로를 요청 하면
     * Then 최단 경로와 거리가 조회 된다.
     */
    @DisplayName("지하철 경로를 조회")
    @Test
    void getPath() {
        // when
        ExtractableResponse<Response> response = 지하철_경로_조회(createPathReadParams(교대역, 양재역));

        // then
        JsonPath responseBody = response.jsonPath();
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(responseBody.getList("stations.id", Long.class)).isEqualTo(List.of(교대역, 강남역, 양재역)),
                () -> assertThat(responseBody.getDouble("distance")).isEqualTo(교대역_강남역 + 강남역_양재역)
        );
    }

    /**
     * When 연결이 되어 있지 않은 출발 역과 도착 역으로 최단 경로를 요청 하면
     * Then 지하철 경로 조회가 실패 된다.
     */
    @DisplayName("연결이 되어 있지 않은 지하철 경로 조회")
    @Test
    void getUnconnectedPath() {
        // when
        Long 사당역 = getIdFromResponse(지하철역_생성_요청("사당역"));
        int responseStatusCode = 지하철_경로_조회(createPathReadParams(강남역, 사당역)).statusCode();

        // then
        assertThat(responseStatusCode).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 존재 하지 않은 출발 역이나 도착 역으로 최단 경로를 요청 하면
     * Then 지하철 경로 조회가 실패 된다.
     */
    @DisplayName("존재 하지 않은 지하철 경로 조회")
    @Test
    void getNonexistentPath() {
        // when
        Long nonexistentStation = 999L;
        int responseStatusCode = 지하철_경로_조회(createPathReadParams(강남역, nonexistentStation)).statusCode();

        // then
        assertThat(responseStatusCode).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
