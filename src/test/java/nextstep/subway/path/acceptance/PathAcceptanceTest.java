package nextstep.subway.path.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.path.acceptance.PathSteps.지하철_역간_최단경로_조회_요청;
import static nextstep.subway.path.acceptance.PathSteps.지하철_역간_최단경로_조회됨;


import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.line.acceptance.LineSteps.지하철_노선에_지하철역_등록_요청;
import static nextstep.subway.station.StationSteps.지하철역_등록되어_있음;
import static nextstep.subway.line.acceptance.LineSteps.지하철_노선_등록되어_있음;

@DisplayName("지하철 경로 조회")
public class PathAcceptanceTest extends AcceptanceTest {
    private LineResponse 신분당선;
    private LineResponse 이호선;
    private LineResponse 삼호선;
    private StationResponse 강남역;
    private StationResponse 양재역;
    private StationResponse 교대역;
    private StationResponse 남부터미널역;

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

        강남역 = 지하철역_등록되어_있음("강남역").as(StationResponse.class);
        양재역 = 지하철역_등록되어_있음("양재역").as(StationResponse.class);
        교대역 = 지하철역_등록되어_있음("교대역").as(StationResponse.class);
        남부터미널역 = 지하철역_등록되어_있음("남부터미널역").as(StationResponse.class);

        이호선 = 지하철_노선_등록되어_있음(lineCreateParams("이호선", "green", 교대역.getId(), 강남역.getId(), 10)).as(LineResponse.class);
        신분당선 = 지하철_노선_등록되어_있음(lineCreateParams("신분당선", "bg-red-600", 강남역.getId(), 양재역.getId(), 10)).as(LineResponse.class);
        삼호선 = 지하철_노선_등록되어_있음(lineCreateParams("삼호선", "orange", 교대역.getId(), 양재역.getId(), 5)).as(LineResponse.class);
        지하철_노선에_지하철역_등록_요청(삼호선, 교대역, 남부터미널역, 3);

    }

    private Map<String, String> lineCreateParams(String name, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }

    @DisplayName("경로, 최단거리 조회 성공")
    @Test
    void getShortestPath() {

        // when
        ExtractableResponse<Response> response = 지하철_역간_최단경로_조회_요청(교대역, 양재역);

        // then
        지하철_역간_최단경로_조회됨(response);
    }



}
