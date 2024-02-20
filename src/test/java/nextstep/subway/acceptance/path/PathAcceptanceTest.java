package nextstep.subway.acceptance.path;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.util.CommonAcceptanceTest;
import nextstep.subway.common.Constant;
import nextstep.subway.path.presentation.response.FindPathResponse;
import nextstep.subway.station.service.dto.ShowStationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.acceptance.line.LineAcceptanceStep.지하철_노선_생성됨;
import static nextstep.subway.acceptance.path.PathAcceptanceStep.지하철_최단_경로_조회;
import static nextstep.subway.acceptance.section.SectionAcceptanceStep.지하철_구간_추가됨;
import static nextstep.subway.acceptance.station.StationAcceptanceStep.지하철_역_생성됨;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 검색")
class PathAcceptanceTest extends CommonAcceptanceTest {

    private Long 교대역_ID;
    private Long 강남역_ID;
    private Long 양재역_ID;
    private Long 남부터미널역_ID;
    private Long 역삼역_ID;
    private Long 강남구청역_ID;
    private Long 압구정로데오역_ID;
    private Long 을지로입구역_ID;

    private Long 이호선_ID;
    private Long 삼호선_ID;
    private Long 신분당선_ID;
    private Long 수인분당선_ID;

    @BeforeEach
    protected void setUp() {
        교대역_ID = 지하철_역_생성됨(Constant.교대역);
        강남역_ID = 지하철_역_생성됨(Constant.강남역);
        양재역_ID = 지하철_역_생성됨(Constant.양재역);
        남부터미널역_ID = 지하철_역_생성됨(Constant.남부터미널역);
        역삼역_ID = 지하철_역_생성됨(Constant.역삼역);
        압구정로데오역_ID = 지하철_역_생성됨(Constant.압구정로데오역);
        강남구청역_ID = 지하철_역_생성됨(Constant.강남구청역);
        을지로입구역_ID = 지하철_역_생성됨(Constant.을지로입구역);

        이호선_ID = 지하철_노선_생성됨(Constant.이호선, Constant.초록색, 교대역_ID, 강남역_ID, Constant.역_간격_15);
        지하철_구간_추가됨(강남역_ID, 역삼역_ID, Constant.역_간격_15, 이호선_ID);
        삼호선_ID = 지하철_노선_생성됨(Constant.삼호선, Constant.주황색, 교대역_ID, 남부터미널역_ID, Constant.역_간격_10);
        지하철_구간_추가됨(남부터미널역_ID, 양재역_ID, Constant.역_간격_10, 삼호선_ID);
        신분당선_ID = 지하철_노선_생성됨(Constant.신분당선, Constant.빨간색, 강남역_ID, 양재역_ID, Constant.역_간격_10);
        수인분당선_ID = 지하철_노선_생성됨(Constant.수인분당선, Constant.노란색, 압구정로데오역_ID, 강남구청역_ID, Constant.역_간격_20);
    }

    /**
     * 교대역    --- *2호선* (15) ---   강남역    --- *2호선* (15) ---   역삼역
     * |                              |
     * *3호선*(10)                    *신분당선*(10)
     * |                              |
     * 남부터미널역  --- *3호선*(10) ---  양재역
     *
     * 압구정로데오 --- *수인분당선*(20) --- 강남구청역
     */

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
        지하철_경로_조회_검증(경로_조회_응답, List.of(Constant.교대역, Constant.강남역, Constant.역삼역), Constant.역_간격_15 + Constant.역_간격_15);
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
        지하철_경로_조회_검증(경로_조회_응답, List.of(Constant.교대역, Constant.남부터미널역, Constant.양재역), Constant.역_간격_10 + Constant.역_간격_10);
    }

    /**
     * When 출발역과 도착역이 동일할 경우
     * Then 경로가 조회되지 않는다.
     */
    @DisplayName("출발역과 도착역이 동일할 경우 경로를 조회할 수 없다.")
    @Test
    void 출발역과_도착역이_동일하게_경로_조회() {
        // when
        ExtractableResponse<Response> 경로_조회_응답 = 지하철_최단_경로_조회(강남역_ID, 강남역_ID);

        // then
        지하철_경로_조회_예외발생_검증(경로_조회_응답, HttpStatus.BAD_REQUEST);
    }

    /**
     * When 존재하지 않은 출발역이나 도착역을 조회 할 경우
     * Then 경로가 조회되지 않는다.
     */
    @DisplayName("존재하지 않은 출발역이나 도착역을 조회 할 경우 경로를 조회할 수 없다.")
    @Test
    void 존재하지_않은_출발역이나_도착역_경로_조회() {
        // when
        ExtractableResponse<Response> 경로_조회_응답 = 지하철_최단_경로_조회(강남역_ID, 을지로입구역_ID);

        // then
        지하철_경로_조회_예외발생_검증(경로_조회_응답, HttpStatus.NOT_FOUND);
    }

    /**
     * When 출발역과 도착역이 연결이 되어 있지 않은 경우
     * Then 경로가 조회되지 않는다.
     */
    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우 경로를 조회할 수 없다.")
    @Test
    void 연결되지_않은_출발역과_도착역_경로_조회() {
        // when
        ExtractableResponse<Response> 경로_조회_응답 = 지하철_최단_경로_조회(강남역_ID, 압구정로데오역_ID);

        // then
        지하철_경로_조회_예외발생_검증(경로_조회_응답, HttpStatus.BAD_REQUEST);
    }

    void 지하철_경로_조회_검증(FindPathResponse findPathResponse, List<String> stationNames, int distance) {
        List<ShowStationDto> stations = findPathResponse.getStations();

        assertThat(findPathResponse.getDistance()).isEqualTo(distance);
        assertThat(stations).hasSize(stationNames.size());
        assertThat(stations.stream()
                .map(stationDto -> stationDto.getName())
                .collect(Collectors.toList())
        ).isEqualTo(stationNames);
    }

    void 지하철_경로_조회_예외발생_검증(ExtractableResponse<Response> extractableResponse, HttpStatus status) {
        assertThat(extractableResponse.statusCode()).isEqualTo(status.value());
    }

}
