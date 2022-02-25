package nextstep.subway.path;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.path.PathSteps.지하철_노선_생성_요청;
import static nextstep.subway.path.PathSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.path.PathSteps.지하철_최단경로_조회_요청;
import static nextstep.subway.path.PathSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;


class PathAcceptanceTest extends AcceptanceTest {
    private StationResponse 교대역;
    private StationResponse 강남역;
    private StationResponse 양재역;
    private StationResponse 남부터미널역;
    private LineResponse 이호선;
    private LineResponse 신분당선;
    private LineResponse 삼호선;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역");
        강남역 = 지하철역_생성_요청("강남역");
        양재역 = 지하철역_생성_요청("양재역");
        남부터미널역 = 지하철역_생성_요청("남부터미널역");

        이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10);
        신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10);
        삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2);

        지하철_노선에_지하철_구간_생성_요청(삼호선, 남부터미널역, 양재역, 3);
    }

    @DisplayName("출발역부터 도착역까지의 최단 경로를 조회할 수 있다.")
    @Test
    void findShortestPath() {
        // when
        ExtractableResponse<Response> response = 지하철_최단경로_조회_요청(교대역, 강남역);

        // then
        지하철_최단경로_응답됨(response);
    }

    @DisplayName("출발역과 도착역이 같은 경우 최단 경로 조회에 실패한다.")
    @Test
    void sameSourceAndTarget() {
        // when
        ExtractableResponse<Response> response = 지하철_최단경로_조회_요청(강남역, 강남역);

        // then
        지하철_최단경로_조회_실패됨(response);
    }

    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우 최단 경로 조회에 실패한다.")
    @Test
    void pathNotConnected() {
        // given
        StationResponse 사당역 = 지하철역_생성_요청("사당역");
        StationResponse 이수역 = 지하철역_생성_요청("이수역");
        LineResponse 사호선 = 지하철_노선_생성_요청("4호선", "blue", 사당역, 이수역, 2);

        // when
        ExtractableResponse<Response> response = 지하철_최단경로_조회_요청(강남역, 사당역);

        // then
        지하철_최단경로_조회_실패됨(response);
    }

    @DisplayName("존재하지 않은 출발역이나 도착역을 조회 할 경우 최단 경로 조회에 실패한다.")
    @Test
    void sourceTargetNotFound() {
        // given
        StationResponse 역삼역 = StationResponse.of(new Station("역삼역"));

        //when
        ExtractableResponse<Response> response = 지하철_최단경로_조회_요청(강남역, 역삼역);

        // then
        지하철_최단경로_조회_실패됨(response);
    }

    private void 지하철_최단경로_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getInt("distance")).isNotNull();
    }

    private void 지하철_최단경로_조회_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
