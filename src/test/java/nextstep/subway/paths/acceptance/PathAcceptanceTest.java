package nextstep.subway.paths.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.paths.dto.PathResponse;
import nextstep.subway.paths.dto.PathStationResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.line.acceptance.LineSteps.*;
import static nextstep.subway.station.StationSteps.지하철역_등록되어_있음;
import static nextstep.subway.paths.acceptance.PathSteps.최단거리_조회_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 검색")
public class PathAcceptanceTest extends AcceptanceTest {
    private LineResponse 신분당선;
    private LineResponse 이호선;
    private LineResponse 삼호선;
    private LineResponse 사호선;
    private StationResponse 강남역;
    private StationResponse 양재역;
    private StationResponse 교대역;
    private StationResponse 남부터미널역;
    private StationResponse 사당역;
    private StationResponse 남태령;

    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_등록되어_있음("강남역").as(StationResponse.class);
        양재역 = 지하철역_등록되어_있음("양재역").as(StationResponse.class);
        교대역 = 지하철역_등록되어_있음("교대역").as(StationResponse.class);
        남부터미널역 = 지하철역_등록되어_있음("남부터미널역").as(StationResponse.class);

        신분당선 = 지하철_노선_등록되어_있음("신분당선", "bg-red-600", 강남역, 양재역, 10).as(LineResponse.class);
        이호선 = 지하철_노선_등록되어_있음("이호선", "bg-red-600", 교대역, 강남역, 10).as(LineResponse.class);
        삼호선 = 지하철_노선_등록되어_있음("삼호선", "bg-red-600", 교대역, 양재역, 5).as(LineResponse.class);

        지하철_노선에_지하철역_등록되어_있음(삼호선, 교대역, 남부터미널역, 3);
    }

    @DisplayName("출발역과 도착역이 다른 경우")
    @Test
    void searchShortestPath() {
        // when
        ExtractableResponse< Response > response = 최단거리_조회_요청(강남역, 남부터미널역);

        // then
        최단경로_찾기_성공(response);
        최단경로_찾기_포함(response, Arrays.asList(강남역, 양재역, 남부터미널역), 12);
    }

    @DisplayName("출발역과 도착역이 같은 경우")
    @Test
    void searchPathFromSameStartAndEndStation() {
        // when
        ExtractableResponse< Response > response = 최단거리_조회_요청(강남역, 강남역);
        // then
        최단경로_찾기_성공(response);
        최단경로_찾기_포함(response, Arrays.asList(강남역), 0);
    }

    @DisplayName("출발역과 도착역이 연결되어 있지 않은 경우")
    @Test
    void searchPathDoesNotExist() {
        // given
        사당역 = 지하철역_등록되어_있음("사당역").as(StationResponse.class);
        남태령 = 지하철역_등록되어_있음("남태령").as(StationResponse.class);
        사호선 = 지하철_노선_등록되어_있음("사호선", "blue", 사당역, 남태령, 20).as(LineResponse.class);

        // when
        ExtractableResponse< Response > response = 최단거리_조회_요청(강남역, 사당역);

        // then
        최단경로_찾기_실패(response);
    }

    @DisplayName("출발역과 도착역 중 등록되지 않은 역이 있을 경우")
    @Test
    void searchFailedForUnRegisteredStation() {
        // given
        final StationResponse 광교역 = new StationResponse(-1L, "광교역", null, null);

        // when
        ExtractableResponse< Response > response = 최단거리_조회_요청(강남역, 광교역);

        // then
        등록되지_않은_지하철역(response);
    }

    private void 최단경로_찾기_성공(ExtractableResponse< Response > response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 최단경로_찾기_실패(ExtractableResponse< Response > response){
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 최단경로_찾기_포함(ExtractableResponse< Response > response, List< StationResponse > expectedStationResponse, int distance) {
        final PathResponse pathResponse = response.as(PathResponse.class);
        final List< PathStationResponse > expectStation = expectedStationResponse.stream()
                .map(it -> new PathStationResponse(it.getId(), it.getName(), it.getCreatedDate()))
                .collect(Collectors.toList());

        assertThat(pathResponse.getStations()).isEqualTo(expectStation);
        assertThat(pathResponse.getDistance()).isEqualTo(distance);
    }

    private void 등록되지_않은_지하철역(ExtractableResponse< Response > response){
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

}
