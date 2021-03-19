package nextstep.subway.path.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.line.acceptance.LineSteps.지하철_노선_등록되어_있음;
import static nextstep.subway.line.acceptance.SectionSteps.지하철_노선_구간_등록되어_있음;
import static nextstep.subway.path.acceptance.PathSteps.지하철_최단경로_응답_확인;
import static nextstep.subway.path.acceptance.PathSteps.지하철_최단경로_조회_요청;
import static nextstep.subway.station.StationSteps.지하철역_등록되어_있음;

@DisplayName("지하철 최단 경로 조회 인수 테스트")
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
    void setup() {
        교대역 = 지하철역_등록되어_있음("교대역").as(StationResponse.class);
        강남역 = 지하철역_등록되어_있음("강남역").as(StationResponse.class);
        양재역 = 지하철역_등록되어_있음("양재역").as(StationResponse.class);
        남부터미널역 = 지하철역_등록되어_있음("남부터미널역").as(StationResponse.class);

        신분당선 = 지하철_노선_등록되어_있음(new LineRequest("신분당선", "red", 강남역.getId(), 양재역.getId(), 3)).as(LineResponse.class);
        이호선 = 지하철_노선_등록되어_있음(new LineRequest("이호선", "green", 교대역.getId(), 강남역.getId(), 5)).as(LineResponse.class);
        삼호선 = 지하철_노선_등록되어_있음(new LineRequest("삼호선", "orange", 교대역.getId(), 양재역.getId(), 8)).as(LineResponse.class);

        SectionRequest sectionRequest = SectionRequest.of(교대역.getId(), 남부터미널역.getId(), 3);
        지하철_노선_구간_등록되어_있음(sectionRequest, 삼호선.getId());
    }

    @DisplayName("출발역부터 도착역까지의 최단 경로 조회")
    @Test
    void shortPath() {
        ExtractableResponse<Response> response = 지하철_최단경로_조회_요청(교대역.getId(), 강남역.getId());
        지하철_최단경로_응답_확인(response.statusCode(), HttpStatus.OK);
    }

    @DisplayName("출발역과 도착역이 동일한 경우 예외처리")
    @Test
    void firstStationEqualsFinalStationException() {
        ExtractableResponse<Response> response = 지하철_최단경로_조회_요청(교대역.getId(), 교대역.getId());
        지하철_최단경로_응답_확인(response.statusCode(), HttpStatus.BAD_REQUEST);
    }

    @DisplayName("출발역과 도착역 연결되어 있지 않은 경우 예외처리")
    @Test
    void notConnectedException() {
        StationResponse 의정부역 = 지하철역_등록되어_있음("의정부역").as(StationResponse.class);
        StationResponse 창동역 = 지하철역_등록되어_있음("창동역").as(StationResponse.class);

        지하철_노선_등록되어_있음(new LineRequest("1호선", "blue", 의정부역.getId(), 창동역.getId(), 4));

        ExtractableResponse<Response> response = 지하철_최단경로_조회_요청(강남역.getId(), 창동역.getId());
        지하철_최단경로_응답_확인(response.statusCode(), HttpStatus.BAD_REQUEST);
    }

    @DisplayName("존재하지 않는 출발역 또는 도착역인 경우 예외처리")
    @Test
    void stationNotFoundException() {
        long source = 50L;
        long target = 51L;
        ExtractableResponse<Response> response = 지하철_최단경로_조회_요청(source, target);
        지하철_최단경로_응답_확인(response.statusCode(), HttpStatus.BAD_REQUEST);
    }
}
