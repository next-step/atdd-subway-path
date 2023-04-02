package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathSteps.지하철_노선_최단거리_조회_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("지하철 경로 검색")
class PathAcceptanceTest extends AcceptanceTest{

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
    public void setUp(){
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

        이호선 = 지하철_노선_생성_요청(createLineCreateParams("2호선", "green", 교대역, 강남역, 10)).jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청(createLineCreateParams("신분당선", "red", 강남역, 양재역, 10)).jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청(createLineCreateParams("3호선", "orange", 교대역, 남부터미널역, 2)).jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    /*
    * when 출발역과 도착역을 입력하여 최단거리 조회를 요청하면
    * then 조회한 구간까지의 최단거리를 알 수 있다.
    * */

    @DisplayName("최단거리 조회 인수테스트")
    @Test
    void getShortPath(){

        // when
        ExtractableResponse<Response> response = 지하철_노선_최단거리_조회_요청(교대역, 양재역);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("distance")).isEqualTo(5)
        );
    }

    /*
    * when 출발역과 도착역이 같을 경우
    * then 최단거리 조회에 실패한다.
    * */

    @DisplayName("출발역과 도착역이 같을 경우 예외가 발생하는 인수테스트")
    @Test
    void getShortPathException1() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_최단거리_조회_요청(교대역, 교대역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }

    /*
     * when 출발역과 도착역이 연결 되어 있지 않을 경우
     * then 최단거리 조회에 실패한다.
     * */

    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우 예외가 발생하는 인수테스트")
    @Test
    void getShortPathException2() {
        // when
        long 부발역 = 지하철역_생성_요청("부발역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선_최단거리_조회_요청(교대역, 부발역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    /*
     * when 출발역이나 도착역이 존재하지 않는다면
     * then 최단거리 조회에 실패한다.
     * */

    @DisplayName("출발역이나 도착역이 존재하지 않는다면 예외가 발생하는 인수테스트")
    @Test
    void getShortPathException3() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_최단거리_조회_요청(교대역, 15L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }








    private Map<String, String> createLineCreateParams(String name, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", name + "");
        lineCreateParams.put("color", color +"");
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
