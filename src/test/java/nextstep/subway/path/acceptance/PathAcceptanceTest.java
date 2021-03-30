package nextstep.subway.path.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.PathResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static nextstep.subway.line.acceptance.LineSteps.지하철_노선_등록되어_있음;
import static nextstep.subway.path.acceptance.PathSteps.지하철_경로_조회_요청;
import static nextstep.subway.line.acceptance.SectionSteps.지하철_노선에_지하철역_등록되어_있음;
import static nextstep.subway.station.StationSteps.지하철역_등록되어_있음;
import static org.assertj.core.api.Assertions.assertThat;

public class PathAcceptanceTest extends AcceptanceTest {

    private LineResponse 신분당선;
    private LineResponse 이호선;
    private LineResponse 삼호선;
    private StationResponse 강남역;
    private StationResponse 양재역;
    private StationResponse 교대역;
    private StationResponse 남부터미널역;
    private Map<String, String> lineCreateParams;

    /**
     * 교대역  --5-- *2호선* ---   강남역
     * |                        |
     * *3호선* -7                   *신분당선* -10
     * |                        |
     * 남부터미널역 --3-- *3호선* -- 양재역
     */

    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_등록되어_있음("강남역").as(StationResponse.class);
        양재역 = 지하철역_등록되어_있음("양재역").as(StationResponse.class);
        교대역 = 지하철역_등록되어_있음("교대역").as(StationResponse.class);
        남부터미널역 = 지하철역_등록되어_있음("남부터미널역").as(StationResponse.class);

        lineCreateParams = createLindCreateParams("신분당선", "bg-red-600", 강남역.getId(), 양재역.getId(), 10);
        신분당선 = 지하철_노선_등록되어_있음(lineCreateParams).as(LineResponse.class);

        lineCreateParams = createLindCreateParams("2호선", "bg-green-600", 교대역.getId(), 강남역.getId(), 5);
        이호선 = 지하철_노선_등록되어_있음(lineCreateParams).as(LineResponse.class);

        lineCreateParams = createLindCreateParams("3호선", "bg-orange-600", 교대역.getId(), 남부터미널역.getId(), 7);
        삼호선 = 지하철_노선_등록되어_있음(lineCreateParams).as(LineResponse.class);

        지하철_노선에_지하철역_등록되어_있음(삼호선, 남부터미널역, 양재역, 3);
    }

    private Map<String, String> createLindCreateParams(String lineName, String color, Long upstationId, Long downStationId, Integer distance) {
        Map<String, String> lineCreateParams = new HashMap<String, String>();
        lineCreateParams.put("name", lineName);
        lineCreateParams.put("color", color);
        lineCreateParams.put("upStationId", String.valueOf(upstationId));
        lineCreateParams.put("downStationId", String.valueOf(downStationId));
        lineCreateParams.put("distance", String.valueOf(distance));

        return lineCreateParams;
    }

    @DisplayName("지하철 경로 조회 - 정상")
    @Test
    public void getPath() {
        // given
        StationResponse source = 강남역;
        StationResponse target = 남부터미널역;

        // when
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(source, target);

        // then
        지하철_경로_응답됨(response);
        지하철_경로_일치됨(response);
    }

    @DisplayName("출발역과 도착역이 같은 경우 경로 조회 - 예외")
    @Test
    public void getPathExceptionThatEquals() {
        // given
        StationResponse source = 강남역;
        StationResponse target = 강남역;

        // when
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(source, target);

        // then
        지하철_경로_응답_실패됨(response);
    }

    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우의 경로 조회 - 예외")
    @Test
    public void getPathExceptionThatDoesNotConnected() {
        // given
        StationResponse 수서역 = 지하철역_등록되어_있음("수서역").as(StationResponse.class);
        StationResponse 가천대역 = 지하철역_등록되어_있음("가천대역").as(StationResponse.class);
        lineCreateParams = createLindCreateParams("분당선", "bg-red-600", 수서역.getId(), 가천대역.getId(), 13);
        지하철_노선_등록되어_있음(lineCreateParams).as(LineResponse.class);

        // when
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(남부터미널역, 가천대역);

        // then
        지하철_경로_응답_실패됨(response);
    }

    @DisplayName("존재하지 않은 출발역이나 도착역으로 경로 조회 - 예외")
    @Test
    public void getPathExceptionThatDoesNotExisted() {
        // given
        StationResponse 태평역 = 지하철역_등록되어_있음("태평역").as(StationResponse.class);
        StationResponse 가천대역 = 지하철역_등록되어_있음("가천대역").as(StationResponse.class);

        // when
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(태평역, 가천대역);

        // then
        지하철_경로_응답_실패됨(response);
    }

    public void 지하철_경로_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.OK.value());
    }

    public void 지하철_경로_일치됨(ExtractableResponse<Response> response) {
        PathResponse path = response.as(PathResponse.class);

        assertThat(
                path.getStations()
                        .stream()
                        .map(StationResponse::getId)
                        .collect(Collectors.toList())
        )
                .isEqualTo(Arrays.asList(강남역.getId(), 교대역.getId(), 남부터미널역.getId()));
    }

    public void 지하철_경로_응답_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
