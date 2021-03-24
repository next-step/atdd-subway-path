package nextstep.subway.path.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationSteps;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static nextstep.subway.line.acceptance.LineSteps.지하철_노선_등록되어_있음;
import static nextstep.subway.line.acceptance.LineSteps.지하철_노선에_지하철역_등록_요청;
import static nextstep.subway.path.acceptance.PathVerifier.*;
import static nextstep.subway.path.acceptance.PathSteps.*;

@DisplayName("지하철 경로조회")
public class PathAcceptanceTest extends AcceptanceTest {

    private LineResponse 신분당선;
    private LineResponse 이호선;
    private LineResponse 삼호선;
    private LineResponse 일호선;

    private StationResponse 강남역;
    private StationResponse 양재역;
    private StationResponse 교대역;
    private StationResponse 남부터미널역;

    private StationResponse 대방역;
    private StationResponse 노량진역;
    private StationResponse 용산역;

    @BeforeEach
    public void setup() {
        대방역 = StationSteps.지하철역_등록되어_있음("대방역").as(StationResponse.class);
        노량진역 = StationSteps.지하철역_등록되어_있음("노량진역").as(StationResponse.class);
        용산역 = new StationResponse(1L,"용산역", LocalDateTime.now(), LocalDateTime.now());

        강남역 = StationSteps.지하철역_등록되어_있음("강남역").as(StationResponse.class);
        양재역 = StationSteps.지하철역_등록되어_있음("양재역").as(StationResponse.class);
        교대역 = StationSteps.지하철역_등록되어_있음("교대역").as(StationResponse.class);
        남부터미널역 = StationSteps.지하철역_등록되어_있음("남부터미널역").as(StationResponse.class);


        신분당선 = 지하철_노선_등록되어_있음("신분당선", "bg-red-600", 강남역.getId(), 양재역.getId(), 10);
        일호선 = 지하철_노선_등록되어_있음("일호선", "bg-red-600", 대방역.getId(), 노량진역.getId(), 10);
        이호선 = 지하철_노선_등록되어_있음("이호선", "bg-red-600", 교대역.getId(), 강남역.getId(), 10);
        삼호선 = 지하철_노선_등록되어_있음("삼호선", "bg-red-600", 교대역.getId(), 양재역.getId(), 5);

        지하철_노선에_지하철역_등록_요청(삼호선, 교대역, 남부터미널역, 3);
    }

    @DisplayName("최단 경로 조회")
    @Test
    public void searchShortestPath() {
        //When
        ExtractableResponse<Response> response = 최단_경로_조회요청(강남역, 양재역);

        //Then
        최단경로_조회_목록_응답됨(response);
    }

    @DisplayName("출발역과 도착역이 같을 경우 최단경로를 조회한다")
    @Test
    public void searchShortestPathWhenSameStartAndTargetStation(){
        //When
        ExtractableResponse<Response> response = 최단_경로_조회요청(강남역, 강남역);

        //Then
        최단경로_조회_실패됨(response);
    }

    @DisplayName("출발역과 도착역이 연결되어있지 않은 경우 최단경로를 조회한다")
    @Test
    public void notConnectStartAndArrivalStation(){
        //When
        ExtractableResponse<Response> response = 최단_경로_조회요청(강남역, 대방역);

        //Then
        최단경로_조회_실패됨(response);
    }

    @DisplayName("존재하지 않는 역으로 최단경로를 조회한다.")
    @Test
    public void notExistStation(){
        //When
        ExtractableResponse<Response> response = 최단_경로_조회요청(용산역, 대방역);

        //Then
        최단경로_조회_실패됨(response);
    }




}
