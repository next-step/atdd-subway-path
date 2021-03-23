package nextstep.subway.path.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.line.acceptance.steps.LineSteps.지하철_노선_등록되어_있음;
import static nextstep.subway.line.acceptance.steps.LineSteps.지하철_노선에_지하철역_등록되어_있음;
import static nextstep.subway.path.acceptance.steps.PathSteps.*;
import static nextstep.subway.station.StationSteps.지하철역_등록되어_있음;

@DisplayName("지하철 최단 경로 조회")
public class PathAcceptanceTest extends AcceptanceTest {

    private LineResponse 신분당선, 이호선, 삼호선, 팔호선;
    private StationResponse 강남역, 양재역, 교대역, 남부터미널역, 잠실역, 천호역;

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

        강남역 = 지하철역_등록되어_있음("강남역").as(StationResponse.class);
        양재역 = 지하철역_등록되어_있음("양재역").as(StationResponse.class);
        교대역 = 지하철역_등록되어_있음("교대역").as(StationResponse.class);
        남부터미널역 = 지하철역_등록되어_있음("남부터미널역").as(StationResponse.class);
        잠실역 = 지하철역_등록되어_있음("천호역").as(StationResponse.class);
        천호역 = 지하철역_등록되어_있음("천호역").as(StationResponse.class);

        신분당선 = 지하철_노선_등록되어_있음(new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 양재역.getId(), 10)).as(LineResponse.class);
        이호선 = 지하철_노선_등록되어_있음(new LineRequest("이호선", "bg-red-600", 교대역.getId(), 강남역.getId(), 10)).as(LineResponse.class);
        삼호선 = 지하철_노선_등록되어_있음(new LineRequest("삼호선", "bg-red-600", 교대역.getId(), 양재역.getId(), 5)).as(LineResponse.class);
        팔호선 = 지하철_노선_등록되어_있음(new LineRequest("팔호선", "bg-pink-600", 잠실역.getId(), 천호역.getId(), 5)).as(LineResponse.class);

        지하철_노선에_지하철역_등록되어_있음(삼호선, 교대역, 남부터미널역, 3);
    }

    @DisplayName("최단 경로 조회")
    @Test
    void getShortestPath(){
        // when
        ExtractableResponse<Response> response = 최단거리_조회_요청(강남역, 남부터미널역);
        // then
        최단거리_조회됨(response, 12);
    }

    @DisplayName("[예외처리] 출발역과 도착역이 같은 경우")
    @Test
    void sameSourceAndTarget(){
        // when
        ExtractableResponse<Response> response = 최단거리_조회_요청(강남역, 강남역);
        // then
        최단거리_조회_실패됨(response);
    }

    @DisplayName("[예외처리] 출발역과 도착역이 연결이 되어 있지 않은 경우")
    @Test
    void unconnectedSourceAndTarget(){
        // when
        ExtractableResponse<Response> response = 최단거리_조회_요청(강남역, 천호역);
        // then
        최단거리_조회_실패됨(response);
    }

    @DisplayName("[예외처리] 존재하지 않은 출발역이나 도착역을 조회 할 경우")
    @Test
    void NotExistsSourceOrTarget(){
        // given
        StationResponse 미개통역 = 지하철역_등록되어_있음("미개통역").as(StationResponse.class);

        // when
        ExtractableResponse<Response> response = 최단거리_조회_요청(강남역, 미개통역);

        // then
        최단거리_조회_실패됨(response);
    }

}