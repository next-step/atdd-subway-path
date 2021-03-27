package nextstep.subway.path.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.path.PathResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.line.acceptance.LineSteps.*;
import static nextstep.subway.path.acceptance.PathSteps.경로_조회_요청;
import static nextstep.subway.station.StationSteps.지하철역_등록되어_있음;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("경로 관련 인수 테스트")
public class PathAcceptanceTest extends AcceptanceTest {

    private StationResponse 강남역;
    private StationResponse 양재역;
    private StationResponse 교대역;
    private StationResponse 남부터미널역;
    private StationResponse 선릉역;
    private LineRequest 신분당선_요청;
    private LineRequest 이호선_요청;
    private LineRequest 삼호선_요청;
    private LineResponse 신분당선;
    private LineResponse 이호선;
    private LineResponse 삼호선;

    @BeforeEach
    public void beforeEach() {
        super.setUp();
        강남역 = 지하철역_등록되어_있음("강남역").as(StationResponse.class);
        양재역 = 지하철역_등록되어_있음("양재역").as(StationResponse.class);
        교대역 = 지하철역_등록되어_있음("교대역").as(StationResponse.class);
        남부터미널역 = 지하철역_등록되어_있음("남부터미널역").as(StationResponse.class);
        선릉역 = 지하철역_등록되어_있음("선릉역").as(StationResponse.class);

        신분당선_요청 = 지하철_노선_요청("신분당선", "bg-red-600", 강남역, 양재역, 10);
        이호선_요청 = 지하철_노선_요청("이호선", "bg-red-600", 양재역, 남부터미널역, 10);
        삼호선_요청 = 지하철_노선_요청("삼호선", "bg-red-600", 강남역, 교대역, 5);

        신분당선 = 지하철_노선_등록되어_있음(신분당선_요청).as(LineResponse.class);
        이호선 = 지하철_노선_등록되어_있음(이호선_요청).as(LineResponse.class);
        삼호선 = 지하철_노선_등록되어_있음(삼호선_요청).as(LineResponse.class);

        지하철_노선에_지하철역_등록되어_있음(삼호선, 교대역, 남부터미널역, 3);
    }

    @DisplayName("최단 경로를 조회한다")
    @Test
    void findShortestPath() {
        // given
        Long source = 1L;
        Long target = 4L;

        // when
        ExtractableResponse<Response> response = 경로_조회_요청(source, target);
        
        // then
        경로_조회됨(response);
    }

    @DisplayName("출발역과 도착역이 같을 때 최단 경로를 조회한다")
    @Test
    void findPathWithSameSourceAndTarget() {
        // given
        Long source = 1L;
        Long target = 1L;

        // when
        ExtractableResponse<Response> response = 경로_조회_요청(source, target);

        // then
        경로_조회_실패됨(response);
    }

    @DisplayName("출발역과 도착역이 연결되지 않을 때 최단 경로를 조회한다")
    @Test
    void findPathDisconnectedPath() {
        // given
        Long source = 1L;
        Long target = 5L;

        // when
        ExtractableResponse<Response> response = 경로_조회_요청(source, target);

        // then
        경로_조회_실패됨(response);
    }

    private void 경로_조회_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 경로_조회됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().as(PathResponse.class).getStations()).containsExactly(강남역, 교대역, 남부터미널역);
    }
}
