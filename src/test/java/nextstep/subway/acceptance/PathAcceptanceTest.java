package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.PathSteps.최단_경로_조회하기;
import static nextstep.subway.acceptance.StationSteps.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("최단 경로 관리 기능")
public class PathAcceptanceTest extends AcceptanceTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 동인천역;

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

        교대역 = 지하철역_식별번호_가져오기(지하철역_생성_요청("교대역"));
        강남역 = 지하철역_식별번호_가져오기(지하철역_생성_요청("강남역"));
        양재역 = 지하철역_식별번호_가져오기(지하철역_생성_요청("양재역"));
        남부터미널역 = 지하철역_식별번호_가져오기(지하철역_생성_요청("남부터미널역"));
        동인천역 = 지하철역_식별번호_가져오기(지하철역_생성_요청("동인천역"));

        이호선 = 노선_식별번호_추출(지하철_노선_생성_요청(createLineCreateParams("2호선", "green", 교대역, 강남역, 10)));
        신분당선 = 노선_식별번호_추출(지하철_노선_생성_요청(createLineCreateParams("신분당선", "red", 강남역, 양재역, 10)));
        삼호선 = 노선_식별번호_추출(지하철_노선_생성_요청(createLineCreateParams("3호선", "orange", 교대역, 남부터미널역, 2)));

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
        지하철_노선에_지하철_구간_생성_요청(이호선, createSectionCreateParams(교대역, 강남역, 3));
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 3));
    }

    /**
     * Then 출발지(교대역)과 도착역(양재역)의 최단 경로를 요청하면
     * When 최단 경로를 조회한다.
     */
    @DisplayName("최단 경로(교대역 - 양재역) 조회")
    @Test
    void shortPath() {
        // when
        ExtractableResponse<Response> response = 최단_경로_조회하기(교대역, 양재역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Then 출발지(교대역)과 도착역(교대역)의 최단 경로를 요청하면
     * When 최단 경로 조회가 실패한다
     */
    @DisplayName("동일한 출발지와 도착역으로 최단 경로 조회하면 예외 발생")
    @Test
    void sourceEqualsTargetException() {
        // when
        ExtractableResponse<Response> response = 최단_경로_조회하기(교대역, 교대역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Then 구간이 연결이 안된 출발지(교대역)과 도착역(남부터미널역)으로 최단 거리 요청하면
     * When 최단 경로 조회가 실패한다
     */
    @DisplayName("연결이 안된 구간으로 최단 경로 조회하면 예외 발생")
    @Test
    void notConnectException() {
        // when
        ExtractableResponse<Response> response = 최단_경로_조회하기(교대역, 동인천역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private Map<String, String> createLineCreateParams(String name, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", name);
        lineCreateParams.put("color", color);
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", distance + "");
        return lineCreateParams;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
