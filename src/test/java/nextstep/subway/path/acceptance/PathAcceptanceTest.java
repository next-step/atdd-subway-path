package nextstep.subway.path.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.line.acceptance.LineSteps.지하철_노선_등록되어_있음;
import static nextstep.subway.line.acceptance.LineSteps.지하철_노선에_지하철역_등록_요청;
import static nextstep.subway.path.acceptance.PathSteps.최단거리_조회_요청;
import static nextstep.subway.path.acceptance.PathSteps.최단거리_지하철역_순서_정렬됨;
import static nextstep.subway.station.StationSteps.지하철역_등록되어_있음;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("경로 조회 관련 기능")
public class PathAcceptanceTest extends AcceptanceTest {

    private LineResponse 신분당선;
    private LineResponse 이호선;
    private LineResponse 삼호선;

    private StationResponse 강남역;
    private StationResponse 양재역;
    private StationResponse 교대역;
    private StationResponse 남부터미널역;

    @BeforeEach
    public void setUp() {
        super.setUp();
        /**
         * 교대역    --- *2호선* ---   강남역
         * |                        |
         * *3호선*                   *신분당선*
         * |                        |
         * 남부터미널역  --- *3호선* ---   양재
         */
        강남역 = 지하철역_등록되어_있음("강남역").as(StationResponse.class);
        양재역 = 지하철역_등록되어_있음("양재역").as(StationResponse.class);
        교대역 = 지하철역_등록되어_있음("교대역").as(StationResponse.class);
        남부터미널역 = 지하철역_등록되어_있음("남부터미널역").as(StationResponse.class);

        신분당선 = 지하철_노선_등록되어_있음("신분당선", "bg-red-600", 강남역, 양재역, 10).as(LineResponse.class);
        이호선 = 지하철_노선_등록되어_있음("이호선", "bg-red-600", 교대역, 강남역, 10).as(LineResponse.class);
        삼호선 = 지하철_노선_등록되어_있음("삼호선", "bg-red-600", 교대역, 양재역, 5).as(LineResponse.class);

        지하철_노선에_지하철역_등록_요청(삼호선, 교대역, 남부터미널역, 3);

        /**
         * 교대역   --- 10 ---    강남역
         * |                     |
         * 3                     10
         * |                     |
         * 남부터미널역  --- 2 ---  양재역
         */
    }

    @DisplayName("최단거리를 조회한다")
    @Test
    void findShortestPath() {
        // when
        Map<String, String> paramsToFind = new HashMap<>();
        paramsToFind.put("sourceId", String.valueOf(교대역.getId()));
        paramsToFind.put("targetId", String.valueOf(양재역.getId()));

        ExtractableResponse<Response> response = 최단거리_조회_요청(paramsToFind);

        // then
        최단거리_조회_요청_조회됨(response);
//        최단거리_지하철역_순서_정렬됨(response, Arrays.asList(교대역, 남부터미널역, 양재역));
    }

    private void 최단거리_조회_요청_조회됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("최단거리 조회시 출발역과 도착역이 같은 경우 실패한다")
    @Test
    void failFindShortestPath_SearchStationsSame() {

    }

    @DisplayName("최단거리 조회시 출발역과 도착역이 연결이 되어 있지 않은 경우 실패한다")
    @Test
    void failFindShortestPath_SearchStationsNotConnected() {

    }

    @DisplayName("최단거리 조회시 존재하지 않은 출발역이나 도착역을 조회 할 경우 실패한다")
    @Test
    void failFindShortestPath_SearchStationsNotExist() {

    }
}
