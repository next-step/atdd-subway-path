package nextstep.subway.path.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.exceptions.InvalidPathPointException;
import nextstep.subway.exceptions.NotFoundStationException;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.line.acceptance.LineSteps.지하철_노선_등록되어_있음;
import static nextstep.subway.line.acceptance.LineSteps.지하철_노선에_지하철역_등록_요청;
import static nextstep.subway.path.acceptance.PathSteps.지하철_노선에_지하철역_최단_경로_요청;
import static nextstep.subway.station.StationSteps.지하철역_등록되어_있음;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("경로 탐색 인수 테스트")
public class PathAcceptanceTest extends AcceptanceTest {
    private LineResponse 신분당선;
    private LineResponse 이호선;
    private LineResponse 삼호선;
    private LineResponse 사호선;

    private StationResponse 강남역;
    private StationResponse 교대역;
    private StationResponse 남부터미널역;
    private StationResponse 양재역;
    private StationResponse 금정역;
    private StationResponse 범계역;


    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_등록되어_있음("강남역").as(StationResponse.class);
        양재역 = 지하철역_등록되어_있음("양재역").as(StationResponse.class);
        교대역 = 지하철역_등록되어_있음("교대역").as(StationResponse.class);
        남부터미널역 = 지하철역_등록되어_있음("남부터미널역").as(StationResponse.class);

        범계역 = 지하철역_등록되어_있음("범계역").as(StationResponse.class);
        금정역 = 지하철역_등록되어_있음("금정역").as(StationResponse.class);

        신분당선 = 지하철_노선_등록되어_있음("신분당선", "bg-red-600", 강남역.getId(), 양재역.getId(), 7);
        이호선 = 지하철_노선_등록되어_있음("2호선", "bg-red-600", 교대역.getId(), 강남역.getId(), 6);
        삼호선 = 지하철_노선_등록되어_있음("3호선", "bg-red-600", 교대역.getId(), 양재역.getId(), 5);
        사호선 = 지하철_노선_등록되어_있음("4호선", "bg-red-600", 금정역.getId(), 범계역.getId(), 5);

        지하철_노선에_지하철역_등록_요청(삼호선, 교대역, 남부터미널역, 3);
    }

    @DisplayName("두 개의 역간의 최단 거리를 조회한다.")
    @Test
    void searchShortestDistanceStation() {
        //when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_최단_경로_요청(양재역.getId(), 교대역.getId());

        //then
        PathResponse pathResponse = response.as(PathResponse.class);
        assertThat(pathResponse.getStations()).hasSize(3);
        assertThat(pathResponse.getStations()).containsExactly(양재역, 남부터미널역, 교대역);
        assertThat(pathResponse.getDistance()).isEqualTo(5.0);
    }

    @DisplayName("출발역과 도착역이 같은경우 검색은 실패한다.")
    @Test
    void searchPathFinderWithDuplicatePoints() {
        //when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_최단_경로_요청(양재역.getId(), 양재역.getId());

        //then
        PathSteps.지하철_노선에_지하철역_최단_경로_요청_실패됨(response);
        PathSteps.에러_응답_메세지_비교(response, InvalidPathPointException.DEFAULT_MSG);
    }

    @DisplayName("출발역과 도착역이 연결되어있지 않을 경우 검색은 실패한다.")
    @Test
    void searchPathFinderNotConnectedPoints() {
        //when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_최단_경로_요청(범계역.getId(), 양재역.getId());

        //when
        PathSteps.지하철_노선에_지하철역_최단_경로_요청_실패됨(response);
    }

    @DisplayName("존재하지 않는 출발역이나 도착역을 조회할 경우 검색은 실패한다.")
    @Test
    void searchPathFindWithNotExistsStation() {
        //when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_최단_경로_요청(10L, 11L);

        //then
        PathSteps.지하철_노선에_지하철역_최단_경로_요청_실패됨(response);
        PathSteps.에러_응답_메세지_비교(response, NotFoundStationException.DEFAULT_MSG);

    }
}
