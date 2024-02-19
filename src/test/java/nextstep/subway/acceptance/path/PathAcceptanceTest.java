package nextstep.subway.acceptance.path;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.util.CommonAcceptanceTest;
import nextstep.subway.common.Constant;
import nextstep.subway.line.presentation.request.AddSectionRequest;
import nextstep.subway.line.presentation.request.CreateLineRequest;
import nextstep.subway.line.presentation.response.CreateLineResponse;
import nextstep.subway.path.presentation.response.FindPathResponse;
import nextstep.subway.station.presentation.request.CreateStationRequest;
import nextstep.subway.station.presentation.response.CreateStationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.stream.Collectors;

import static nextstep.subway.acceptance.line.LineAcceptanceStep.지하철_노선_생성;
import static nextstep.subway.acceptance.path.PathAcceptanceStep.지하철_최단_경로_조회;
import static nextstep.subway.acceptance.section.SectionAcceptanceStep.지하철_구간_추가;
import static nextstep.subway.acceptance.station.StationAcceptanceStep.지하철_역_생성;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 검색")
class PathAcceptanceTest extends CommonAcceptanceTest {

    private Long 교대역_ID;
    private Long 강남역_ID;
    private Long 양재역_ID;
    private Long 남부터미널역_ID;
    private Long 역삼역_ID;

    private Long 이호선_ID;
    private Long 삼호선_ID;
    private Long 신분당선_ID;

    /**
     * 교대역    --- *2호선* ---   강남역   --- *2호선* ---   역삼역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재역
     */

    @BeforeEach
    protected void setUp() {
        교대역_ID = 지하철_역_생성(CreateStationRequest.from(Constant.교대역)).as(CreateStationResponse.class).getStationId();
        강남역_ID = 지하철_역_생성(CreateStationRequest.from(Constant.강남역)).as(CreateStationResponse.class).getStationId();
        양재역_ID = 지하철_역_생성(CreateStationRequest.from(Constant.양재역)).as(CreateStationResponse.class).getStationId();
        남부터미널역_ID = 지하철_역_생성(CreateStationRequest.from(Constant.남부터미널역)).as(CreateStationResponse.class).getStationId();
        역삼역_ID = 지하철_역_생성(CreateStationRequest.from(Constant.역삼역)).as(CreateStationResponse.class).getStationId();

        이호선_ID = 지하철_노선_생성(CreateLineRequest.of(Constant.이호선, Constant.초록색, 교대역_ID, 강남역_ID, Constant.역_간격_15)).as(CreateLineResponse.class).getLineId();
        지하철_구간_추가(AddSectionRequest.of(강남역_ID, 역삼역_ID, Constant.역_간격_15), 이호선_ID);
        삼호선_ID = 지하철_노선_생성(CreateLineRequest.of(Constant.삼호선, Constant.주황색, 교대역_ID, 남부터미널역_ID, Constant.역_간격_10)).as(CreateLineResponse.class).getLineId();
        지하철_구간_추가(AddSectionRequest.of(남부터미널역_ID, 양재역_ID, Constant.역_간격_10), 삼호선_ID);
        신분당선_ID = 지하철_노선_생성(CreateLineRequest.of(Constant.신분당선, Constant.빨간색, 강남역_ID, 양재역_ID, Constant.역_간격_10)).as(CreateLineResponse.class).getLineId();
    }

    /**
     * When 같은 노선의 출발역과 도착역의 경로를 검색하면
     * Then 최단 경로를 알려준다.
     */
    @DisplayName("같은 출발역과 도착역의 최단 경로를 조회한다.")
    @Test
    void 같은_노선의_출발역과_도착역의_최단_경로_조회() {
        // when
        FindPathResponse 경로_조회_응답 = 지하철_최단_경로_조회(교대역_ID, 역삼역_ID).as(FindPathResponse.class);

        // then
        assertThat(경로_조회_응답.getDistance()).isEqualTo(Constant.역_간격_15 + Constant.역_간격_15);
        assertThat(경로_조회_응답.getStations()).hasSize(3);
        assertThat(경로_조회_응답.getStations().stream()
                .map(stationDto -> stationDto.getName())
                .collect(Collectors.toList())
        ).containsExactly(Constant.교대역, Constant.강남역, Constant.역삼역);
    }

    /**
     * When 여러 노선의 출발역과 도착역의 경로를 검색하면
     * Then 최단 경로를 알려준다.
     */
    @DisplayName("여러 출발역과 도착역의 최단 경로를 조회한다.")
    @Test
    void 여러_노선의_출발역과_도착역의_최단_경로_조회() {
        // when
        FindPathResponse 경로_조회_응답 = 지하철_최단_경로_조회(교대역_ID, 양재역_ID).as(FindPathResponse.class);

        // then
        assertThat(경로_조회_응답.getDistance()).isEqualTo(Constant.역_간격_10 + Constant.역_간격_10);
        assertThat(경로_조회_응답.getStations()).hasSize(3);
        assertThat(경로_조회_응답.getStations().stream()
                .map(stationDto -> stationDto.getName())
                .collect(Collectors.toList())
        ).containsExactly(Constant.교대역, Constant.남부터미널역, Constant.양재역);
    }



    /**
     * When 출발역과 도착역이 같게 경로를 검색하면
     * Then 경로가 조회되지 않는다.
     */
    @DisplayName("출발역과 도착역이 동일하게는 경로를 조회할 수 없다.")
    @Test
    void 출발역과_도착역을_동일하게_경로_조회() {
        // when
        ExtractableResponse<Response> 경로_조회_응답 = 지하철_최단_경로_조회(강남역_ID, 강남역_ID);

        // then
        지하철_경로_조회_예외발생_검증(경로_조회_응답, HttpStatus.BAD_REQUEST);
    }

    void 지하철_경로_조회_예외발생_검증(ExtractableResponse<Response> extractableResponse, HttpStatus status) {
        assertThat(extractableResponse.statusCode()).isEqualTo(status.value());
    }
}
